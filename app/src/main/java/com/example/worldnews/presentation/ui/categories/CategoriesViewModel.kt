package com.example.worldnews.presentation.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldnews.data.repository.AppDatabaseProvider
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabaseProvider.get(application).articleDao()

    private val api = RetrofitInstance.api

    private val repo = NewsRepositoryImpl(api, dao)

    private val _articles = MutableStateFlow<List<ArticleEntity>>(emptyList())
    val articles: StateFlow<List<ArticleEntity>> = _articles

    private var currentCategory: String? = null

    private var currentPage: Int = 1

    private var isLoading = false

    fun loadCategory(category: String, refresh: Boolean = false) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            if (refresh) {
                currentPage = 1
                currentCategory = category
            }
            try {
                val fetched = repo.fetchHeadlines(currentCategory, currentPage)
                println("Fetched articles: ${fetched.map { it.title }}")
                repo.insertArticles(fetched)
            } catch (e: Exception) {
                println("loadCategory error: ${e.message}")
                }
            viewModelScope.launch {
                val resp = repo.fetchHeadlines("business", 1)
                Log.d("API_TEST", "Fetched ${resp.size} articles: $resp")
                }
            _articles.value = repo.getCachedByCategory(currentCategory)
            isLoading = false
        }
    }

    fun loadMore() {
        if (isLoading) return
        viewModelScope.launch {

            isLoading = true

            currentPage++
            try {
                val fetched = repo.fetchHeadlines(currentCategory, currentPage)

                repo.insertArticles(fetched)

            } catch (e: Exception) { println("loadMore error: ${e.message}") }

            _articles.value = repo.getCachedByCategory(currentCategory)

            isLoading = false
        }
    }
}