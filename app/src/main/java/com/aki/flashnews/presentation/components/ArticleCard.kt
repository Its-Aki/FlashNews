package com.aki.flashnews.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aki.flashnews.domain.model.Article
import com.aki.flashnews.ui.theme.FlashNewsTheme

@Composable
fun ArticleCard(
    article: Article,
    onClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(article) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)
                    // Optional: Add placeholders/error drawables
                    // .placeholder(R.drawable.placeholder)
                    // .error(R.drawable.error_image)
                    .build(),
                contentDescription = "Image for article: ${article.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Adjust height as needed
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.sourceName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Consider formatting the date nicely
                    Text(
                        text = article.publishedAt, // Format this date
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticleCardPreview() {
    FlashNewsTheme {
        ArticleCard(
            article = Article(
                url = "http://example.com",
                sourceName = "Example News",
                author = "John Doe",
                title = "This is a Sample Article Title That Might Be Quite Long",
                description = "This is the description of the sample article. It provides a brief summary of the content.",
                urlToImage = "", // Provide a placeholder URL if needed for preview
                publishedAt = "2023-10-27T10:00:00Z",
                content = "Full content here..."
            ),
            onClick = {}
        )
    }
}
