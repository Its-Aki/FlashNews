package com.aki.flashnews.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String, // Assuming URL is unique enough for a primary key
    val sourceName: String?,
    val author: String?,
    val title: String, // Make essential fields non-nullable if appropriate
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?, // Store as ISO String, parse when displaying
    val content: String?,
    val fetchedAt: Long = System.currentTimeMillis() // To check cache expiry
)