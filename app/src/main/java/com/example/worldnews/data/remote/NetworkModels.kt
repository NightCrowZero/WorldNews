package com.example.worldnews.data.remote

data class NewsResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleDto> = emptyList()
)

data class ArticleDto(
    val source: SourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String?
)

data class SourceDto(
    val id: String?,
    val name: String?
)