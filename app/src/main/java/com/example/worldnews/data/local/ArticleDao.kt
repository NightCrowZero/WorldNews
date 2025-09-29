package com.example.worldnews.data.local

import androidx.room.*

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC LIMIT :limit")
    suspend fun getTopHeadlines(limit: Int = 20): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY page, publishedAt DESC")
    suspend fun getByCategory(category: String?): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE query = :query ORDER BY page, publishedAt DESC")
    suspend fun getByQuery(query: String?): List<ArticleEntity>

    @Query("DELETE FROM articles WHERE category = :category")
    suspend fun clearCategory(category: String?)

    @Query("DELETE FROM articles WHERE query = :query")
    suspend fun clearQuery(query: String?)
}