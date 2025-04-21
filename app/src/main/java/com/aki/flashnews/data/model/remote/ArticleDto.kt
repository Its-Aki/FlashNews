package com.aki.flashnews.data.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsResponse(
    @Json(name = "status") val status: String,
    @Json(name = "totalResults") val totalResults: Int?, // Make nullable if API sometimes omits it
    @Json(name = "articles") val articles: List<ArticleDto>?,
    @Json(name = "code") val code: String? = null, // For error responses
    @Json(name = "message") val message: String? = null // For error responses
)

@JsonClass(generateAdapter = true)
data class ArticleDto(
    @Json(name = "source") val source: SourceDto?,
    @Json(name = "author") val author: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "url") val url: String?, // Use URL as unique identifier if API guarantees it
    @Json(name = "urlToImage") val urlToImage: String?,
    @Json(name = "publishedAt") val publishedAt: String?, // Consider Instant or ZonedDateTime
    @Json(name = "content") val content: String?
)

@JsonClass(generateAdapter = true)
data class SourceDto(
    @Json(name = "id") val id: String?,
    @Json(name = "name") val name: String?
)