package com.example.worldnews.ui

import android.util.Log
import androidx.lifecycle.*
import com.example.worldnews.data.NewsRepository
import com.example.worldnews.data.Article
import com.example.worldnews.data.local.ArticleEntity
import kotlinx.coroutines.launch


class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleEntity>>()
    val articles: LiveData<List<ArticleEntity>> = _articles

    fun loadNews(category: String? = null, page: Int = 1) {

        viewModelScope.launch {

            try {

                val entities: List<ArticleEntity> = repository.fetchHeadlines(category, page)

                repository.insertArticles(entities) // savr to Room

                _articles.postValue(entities)

            } catch (e: Exception) {

                Log.e("API", "Exception: ${e.message}", e)

            }
        }
    }
}



fun ArticleEntity.toArticle() = Article(
    title = title,
    description = description,
    imageUrl = urlToImage,
    url = url
)
