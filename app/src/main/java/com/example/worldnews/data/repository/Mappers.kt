package com.example.worldnews.data.repository

import com.example.worldnews.data.local.ArticleEntity

data class Article(
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val url: String?
)

fun ArticleEntity.toArticle() = Article(
    title = title,
    description = description,
    imageUrl = urlToImage,
    url = url
)