package com.aki.flashnews.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.windowInsets
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aki.flashnews.presentation.components.ArticleCard
import com.aki.flashnews.presentation.components.ErrorState
import com.aki.flashnews.presentation.components.LoadingIndicator
import com.aki.flashnews.presentation.viewmodel.NewsListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel: NewsListViewModel = hiltViewModel(),
    onArticleClick: (String) -> Unit // Pass encoded URL string
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current

    // Show snackbar on error (if not already showing loading/full screen error)
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            if (!state.isLoading && !state.isRefreshing && state.articles.isNotEmpty()) { // Show snackbar only if list is visible
                val result = snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = "Retry",
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onRefresh()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
                    Text(modifier = Modifier.fillMaxWidth().padding(16.dp), text="Top News", style = MaterialTheme.typography.headlineMedium)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(isRefreshing = false, state = pullRefreshState, onRefresh = { viewModel.onRefresh() }) // Apply pull refresh modifier
        ) {
            when {
                // Full screen loading (initial load)
                state.isLoading && state.articles.isEmpty() -> {
                    LoadingIndicator()
                }
                // Full screen error (initial load failed)
                state.error != null && state.articles.isEmpty() && !state.isLoading && !state.isRefreshing -> {
                    ErrorState(
                        message = state.error ?: "An unknown error occurred",
                        onRetry = { viewModel.getTopHeadlines() }
                    )
                }
                // Content available (potentially with background loading/refreshing)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = state.articles,
                            key = { article -> article.url } // Use URL as stable key
                        ) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { clickedArticle ->
                                    // URL encode the article URL before passing as nav argument
                                    val encodedUrl = java.net.URLEncoder.encode(clickedArticle.url, "UTF-8")
                                    onArticleClick(encodedUrl)
                                }
                            )
                        }

                        // Optional: Add loading indicator at the bottom for pagination
                        // if (state.isLoadingMore) { item { LoadingItem() } }
                    }
                }
            }

            // Pull-to-refresh indicator - sits on top of the content
        }
    }
}