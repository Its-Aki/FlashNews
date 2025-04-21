package com.aki.flashnews.presentation.screens

import android.R.attr.contentDescription
import android.R.attr.navigationIcon
import android.R.attr.onClick
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Use autoMirrored for RTL support
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aki.flashnews.R
import com.aki.flashnews.presentation.components.ErrorState
import com.aki.flashnews.presentation.components.LoadingIndicator
import com.aki.flashnews.presentation.viewmodel.NewsDetailViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    viewModel: NewsDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onNavigateBack) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "navigate back" // Add to strings.xml
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(state.article?.sourceName ?: "Article Detail", style = MaterialTheme.typography.headlineSmall)
            }

}
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(message = state.error!!) // Add retry if applicable
                state.article != null -> {
                    val article = state.article!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) // Make content scrollable
                            .padding(16.dp)
                    ) {
                        Text(article.title, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "By ${article.author}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                article.publishedAt, // Format date
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(article.urlToImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Article Image: ${article.title}",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            article.description, // Show description first?
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            article.content, // Show full content if available
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                            context.startActivity(intent)
                        }) {
                            Text(text = "Open in Browser")
                        }
                        // Link to open in browser already in TopAppBar Action
                    }
                }
            }
        }
    }
}