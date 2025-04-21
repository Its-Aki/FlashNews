package com.aki.flashnews.presentation.viewmodel

import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aki.flashnews.domain.model.Article
import com.aki.flashnews.domain.repository.NewsRepository
import com.aki.flashnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class NewsDetailState(
    val isLoading: Boolean = false,
    val article: Article? = null,
    val error: String? = null
)
@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle // Hilt injects this for accessing navigation arguments
) : ViewModel() {

    private val _state = MutableStateFlow(NewsDetailState())
    val state: StateFlow<NewsDetailState> = _state.asStateFlow()

    // Retrieve the argument passed via navigation
    private val articleUrl: String? = savedStateHandle.get<String>("articleUrl")?.let {
        // Decode URL if it was encoded for navigation
        java.net.URLDecoder.decode(it, "UTF-8")
    }


    init {
        loadArticleDetails()
    }

    private fun loadArticleDetails() {
        if (articleUrl == null) {
            _state.value = NewsDetailState(error = "Article URL not provided")
            return
        }
        viewModelScope.launch {
            newsRepository.getArticle(articleUrl)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> _state.value = NewsDetailState(article = resource.data)
                        is Resource.Error -> _state.value = NewsDetailState(error = resource.message ?: "Error loading article")
                        is Resource.Loading -> _state.value = NewsDetailState(isLoading = resource.isLoading)
                    }
                }
        }
    }
}