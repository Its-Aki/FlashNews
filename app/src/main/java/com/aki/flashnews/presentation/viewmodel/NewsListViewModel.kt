package com.aki.flashnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aki.flashnews.domain.model.Article
import com.aki.flashnews.domain.repository.NewsRepository
import com.aki.flashnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsListState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false // Separate flag for pull-to-refresh
)

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsListState())
    val state: StateFlow<NewsListState> = _state.asStateFlow()

    init {
        getTopHeadlines()
    }

    fun getTopHeadlines(forceRefresh: Boolean = false) {
        if (_state.value.isLoading || (forceRefresh && _state.value.isRefreshing)) return // Prevent concurrent fetches

        viewModelScope.launch {
            newsRepository.getTopHeadlines(forceRefresh = forceRefresh)
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = !forceRefresh, // Show full screen loading only on initial load
                            isRefreshing = forceRefresh // Show refresh indicator on pull-to-refresh
                        )
                    }
                }
                .catch { throwable -> // Catch errors in the Flow collection itself
                    _state.update {
                        it.copy(
                            error = throwable.message ?: "An unexpected error occurred collecting flow",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    articles = resource.data ?: emptyList(),
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = null // Clear error on success
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    error = resource.message ?: "An unknown error occurred",
                                    articles = resource.data ?: it.articles, // Show cached data if available
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = resource.isLoading && !forceRefresh,
                                    isRefreshing = resource.isLoading && forceRefresh
                                    // Don't clear error while loading, keep previous state
                                )
                            }
                        }
                    }
                }
        }
    }

    fun onRefresh() {
        getTopHeadlines(forceRefresh = true)
    }
}