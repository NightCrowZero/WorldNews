package com.example.worldnews.domain.repository

import com.example.worldnews.data.local.ArticleEntity

interface NewsRepository {
    suspend fun getTopHeadlines(country: String, category: String? = null): List<ArticleEntity>
    suspend fun searchNews(query: String): List<ArticleEntity>
    suspend fun fetchHeadlines(category: String?, page: Int): List<ArticleEntity>
    suspend fun fetchCategoryViaEverything(category: String, page: Int): List<ArticleEntity>
    suspend fun fetchSearch(query: String, page: Int): List<ArticleEntity>
    suspend fun insertArticles(articles: List<ArticleEntity>)
    suspend fun getCachedByQuery(query: String): List<ArticleEntity>
    suspend fun getCachedHeadlines(): List<ArticleEntity>
    suspend fun getCachedByCategory(category: String?): List<ArticleEntity>
}
