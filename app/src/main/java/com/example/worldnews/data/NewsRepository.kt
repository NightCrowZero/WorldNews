package com.example.worldnews.data

import android.util.Log
import com.example.worldnews.BuildConfig
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.remote.ArticleDto
import com.example.worldnews.data.remote.RetrofitInstance

class NewsRepository(private val dao: ArticleDao) {

    private val api = RetrofitInstance.api

    suspend fun getCachedByCategory(category: String?): List<ArticleEntity> =
        dao.getByCategory(category)

    suspend fun getCachedHeadlines(): List<ArticleEntity> =
        dao.getTopHeadlines()


    suspend fun getCachedByQuery(query: String): List<ArticleEntity> =
        dao.getByQuery(query)

    suspend fun fetchSearch(query: String, page: Int): List<ArticleEntity> {

        val resp = api.searchNews(query = query, page = page,)  //apiKey = BuildConfig.NEWS_API_KEY

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = null, query = query, page = page)
        } ?: emptyList()

        dao.insertAll(articles) // cash to Room

        return articles
    }

    suspend fun fetchCategoryViaEverything(category: String, page: Int): List<ArticleEntity> {

        val resp = api.searchNews(

            query = category,
            page = page

        )

        Log.d("API_TEST", "Response code: ${resp.code()}, body: ${resp.body()}")

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = category, query = null, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles

    }

    suspend fun fetchHeadlines(category: String?, page: Int): List<ArticleEntity> {

        val resp = api.getTopHeadlines(
            country = "us",
            category = category,
            page = page,
            //apiKey = BuildConfig.NEWS_API_KEY
        )

        Log.d("API_TEST", "Response code: ${resp.code()}, body: ${resp.body()}")

        val articles = resp.body()?.articles?.map { dto ->
            dto.toEntity(category = category, query = null, page = page)
        } ?: emptyList()

        dao.insertAll(articles)

        return articles

    }

    suspend fun insertArticles(articles: List<ArticleEntity>) {
        dao.insertAll(articles)
    }

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
