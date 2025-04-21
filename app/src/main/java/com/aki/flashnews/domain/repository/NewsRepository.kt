package com.aki.flashnews.domain.repository

import com.aki.flashnews.domain.model.Article
import com.aki.flashnews.util.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    // Fetches news, trying network first, then falling back to cache if offline/error
    // Emits loading state initially
    fun getTopHeadlines(
        forceRefresh: Boolean = false,
        country: String = "us"
    ): Flow<Resource<List<Article>>>

    // Optional: Search functionality
    // fun searchNews(query: String): Flow<Resource<List<Article>>>

    // Optional: Get single article detail

    fun getArticle(url: String): Flow<Resource<Article?>>
}