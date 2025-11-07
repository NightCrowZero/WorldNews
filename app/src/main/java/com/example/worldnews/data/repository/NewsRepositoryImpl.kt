package com.example.worldnews.data.repository

import android.util.Log
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.remote.ArticleDto
import com.example.worldnews.data.remote.NewsApi
import com.example.worldnews.domain.repository.NewsRepository
import jakarta.inject.Inject

class NewsRepositoryImpl  @Inject constructor (
    private val api: NewsApi,
    private val dao: ArticleDao
) : NewsRepository {

    override suspend fun getCachedByCategory(category: String?): List<ArticleEntity> =
        dao.getByCategory(category)

    override suspend fun clearSearchCache(query: String) {
        dao.clearArticlesForQuery(query)
    }

    suspend fun getCachedHeadlinesPaged(page: Int, pageSize: Int = 20): List<ArticleEntity> {
        val offset = (page - 1) * pageSize
        return dao.getPagedHeadlines(limit = pageSize, offset = offset)
    }

    override suspend fun getCachedByQuery(query: String): List<ArticleEntity> =
        dao.getByQuery(query)

    override suspend fun fetchSearch(query: String, page: Int): List<ArticleEntity> {

        val resp = api.searchNews(query = query, page = page)

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = null, query = query, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles
    }

    override suspend fun fetchCategoryViaEverything(category: String, page: Int): List<ArticleEntity> {

        val resp = api.searchNews(query = category, page = page)

        Log.d("API_TEST", "Response code: ${resp.code()}, body: ${resp.body()}")

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = category, query = null, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles
    }

    override suspend fun getTopHeadlines(
        country: String,
        category: String?
    ): List<ArticleEntity> {
        val resp = api.getTopHeadlines(
            country = country,
            category = category
        )

        return resp.body()?.articles?.map { dto ->
            dto.toEntity(category = category, query = null, page = 1)
        } ?: emptyList()
    }

    override suspend fun searchNews(query: String): List<ArticleEntity> {
        val resp = api.searchNews(query = query, page = 1)
        return resp.body()?.articles?.map { dto ->
            dto.toEntity(category = null, query = query, page = 1)
        } ?: emptyList()
    }

    suspend fun fetchHeadlines(page: Int): List<ArticleEntity> {

        val resp = api.getTopHeadlines(
            country = "us",
            page = page,
        )

        Log.d("API_TEST", "Response code: ${resp.code()}, body: ${resp.body()}")

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(query = null, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles
    }

    override suspend fun fetchHeadlines(category: String?, page: Int): List<ArticleEntity> {

        val resp = api.getTopHeadlines(
            country = "us",
            category = category,
            page = page,
        )

        Log.d("API_TEST", "Response code: ${resp.code()}, body: ${resp.body()}")

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = category, query = null, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles
    }

    override suspend fun insertArticles(articles: List<ArticleEntity>) {
        dao.insertAll(articles)
    }

    private fun ArticleDto.toEntity(query: String?, page: Int) = ArticleEntity(
        url = url,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        sourceName = source?.name,
        category = "",
        query = query,
        page = page
    )
    private fun ArticleDto.toEntity(category: String?, query: String?, page: Int) = ArticleEntity(
        url = url,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        sourceName = source?.name,
        category = category,
        query = query,
        page = page
    )
}
