package com.aki.flashnews.data.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles ORDER BY fetchedAt DESC, publishedAt DESC") // Example ordering
    fun getAllArticles(): Flow<List<ArticleEntity>> // Flow emits updates automatically

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    // Optional: Query for specific article by URL (for detail view maybe)
    @Query("SELECT * FROM articles WHERE url = :articleUrl")
    fun getArticleByUrl(articleUrl: String): Flow<ArticleEntity?>

    // Optional: Delete old articles (e.g., older than 1 day)
    @Query("DELETE FROM articles WHERE fetchedAt < :timestamp")
    suspend fun deleteOldArticles(timestamp: Long)
}