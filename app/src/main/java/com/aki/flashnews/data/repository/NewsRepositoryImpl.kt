package com.aki.flashnews.data.repository


import com.aki.flashnews.data.model.local.ArticleDao
import com.aki.flashnews.data.model.mapper.toDomainModel
import com.aki.flashnews.data.model.mapper.toEntity
import com.aki.flashnews.data.model.remote.NewsApiService
import com.aki.flashnews.domain.model.Article
import com.aki.flashnews.domain.repository.NewsRepository
import com.aki.flashnews.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Make repository a singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {

    // Simple cache expiry (e.g., 1 hour)
    private val CACHE_EXPIRY_MS = 60 * 60 * 1000L

    override fun getTopHeadlines(
        forceRefresh: Boolean,
        country: String
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading(isLoading = true))

        val localArticles = articleDao.getAllArticles() // Get Flow from DAO

        // Check cache first (only need the first emission to decide if refresh is needed)
        val firstLocalEmission = localArticles.firstOrNull()
        val oldestArticleTimestamp = firstLocalEmission?.minOfOrNull { it.fetchedAt } ?: 0L
        val isCacheExpired = (System.currentTimeMillis() - oldestArticleTimestamp) > CACHE_EXPIRY_MS
        val needsNetworkFetch = forceRefresh || firstLocalEmission.isNullOrEmpty() || isCacheExpired

        if (needsNetworkFetch) {
            try {
                val response = apiService.getTopHeadlines(country = country)
                if (response.isSuccessful && response.body() != null) {
                    val remoteArticles = response.body()!!.articles
                    if (remoteArticles != null) {
                        // Clear old cache before inserting new data
                        // articleDao.deleteOldArticles(System.currentTimeMillis() - CACHE_EXPIRY_MS * 2) // Keep some history
                        articleDao.deleteAllArticles() // Simpler: Clear all before inserting fresh headlines
                        articleDao.insertArticles(remoteArticles.mapNotNull { it.toEntity() })
                    } else {
                        // Handle case where API success but no articles array
                        emit(Resource.Error("API returned success but no articles found."))
                    }
                } else {
                    // API call failed (non-2xx response)
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Unknown API error"
                    emit(Resource.Error("API Error: $errorMsg (Code: ${response.code()})", data = firstLocalEmission?.map { it.toDomainModel() })) // Emit cached data with error
                }
            } catch (e: IOException) {
                // Network error (no internet, DNS lookup failed etc.)
                emit(Resource.Error("Network Error: ${e.message}", data = firstLocalEmission?.map { it.toDomainModel() }))
            } catch (e: HttpException) {
                // HTTP error (non-2xx responses already handled, but good practice)
                emit(Resource.Error("HTTP Error: ${e.message}", data = firstLocalEmission?.map { it.toDomainModel() }))
            } catch (e: Exception) {
                // Other unexpected errors (e.g., JSON parsing)
                emit(Resource.Error("Unexpected Error: ${e.message}", data = firstLocalEmission?.map { it.toDomainModel() }))
            }
        }

        // Regardless of network success/failure, emit the latest data from the DB Flow
        // map converts the Flow<List<ArticleEntity>> to Flow<List<Article>>
        // We only want successful emissions from the DB after the loading/initial fetch logic
        // Combine previous error/loading state with DB emission
        val latestLocalArticles = articleDao.getAllArticles().map { entities ->
            Resource.Success(entities.map { it.toDomainModel() })
        }
        emitAll(latestLocalArticles) // emitAll subscribes to the DB Flow
        emit(Resource.Loading(isLoading = false)) // Indicate loading finished after first DB emit
    }

    override fun getArticle(url: String): Flow<Resource<Article?>> = flow {
        emit(Resource.Loading(true))
        try {
            articleDao.getArticleByUrl(url).collect { entity ->
                if (entity != null) {
                    emit(Resource.Success(entity.toDomainModel()))
                } else {
                    // Optionally try fetching from network if not found locally
                    emit(Resource.Error("Article not found in local cache.", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching article: ${e.message}", null))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}