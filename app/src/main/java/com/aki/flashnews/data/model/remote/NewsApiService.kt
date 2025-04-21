package com.aki.flashnews.data.model.remote
import com.aki.flashnews.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    // Example: Top headlines endpoint
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us", // Default country
        @Query("category") category: String? = null, // Optional category
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ): Response<NewsResponse> // Use Response<> for error handling

    // Example: Everything endpoint (searching)
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt", // e.g., relevancy, popularity
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ): Response<NewsResponse>
}