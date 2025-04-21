package com.aki.flashnews.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // If passing between destinations
data class Article(
    val url: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String, // Could be Instant/ZonedDateTime
    val content: String
) : Parcelable