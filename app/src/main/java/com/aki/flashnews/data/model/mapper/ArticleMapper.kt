package com.aki.flashnews.data.model.mapper

import com.aki.flashnews.data.model.local.ArticleEntity
import com.aki.flashnews.data.model.remote.ArticleDto
import com.aki.flashnews.domain.model.Article

fun ArticleDto.toEntity(): ArticleEntity? {
    // Require essential fields to create a valid entity
    if (url == null || title == null) return null
    return ArticleEntity(
        url = url,
        sourceName = source?.name,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun ArticleEntity.toDomainModel(): Article {
    return Article(
        url = url,
        sourceName = sourceName ?: "Unknown Source",
        author = author ?: "Unknown Author",
        title = title, // Assume title is non-null in DB
        description = description ?: "",
        urlToImage = urlToImage ?: "", // Provide default or handle null in UI
        publishedAt = publishedAt ?: "", // Handle date parsing later
        content = content ?: ""
    )
}
fun ArticleDto.toDomainModel(): Article? {
    // Require essential fields for a valid domain model display
    if (url == null || title == null) return null
    return Article(
        url = url,
        sourceName = source?.name ?: "Unknown Source",
        author = author ?: "Unknown Author",
        title = title,
        description = description ?: "",
        urlToImage = urlToImage ?: "",
        publishedAt = publishedAt ?: "",
        content = content ?: ""
    )
}