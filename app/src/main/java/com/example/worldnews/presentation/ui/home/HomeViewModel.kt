package com.example.worldnews.presentation.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldnews.data.repository.AppDatabaseProvider
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabaseProvider.get(application).articleDao()

    private val api = RetrofitInstance.api

    private val repo = NewsRepositoryImpl(api, dao)

    private val _articles = MutableStateFlow<List<ArticleEntity>>(emptyList())

    val articles: StateFlow<List<ArticleEntity>> = _articles

    private var currentQuery: String = ""

    private var currentPage: Int = 1

    private var isLoading = false

    init {
        loadHeadlines(refresh = true)
    }

    fun loadHeadlines(refresh: Boolean = false) {

        if (isLoading) return
        viewModelScope.launch {

            isLoading = true
            if (refresh) currentPage = 1
            currentQuery = ""

            try {

                val fetched = repo.fetchHeadlines(category = null, page = currentPage)
                if (fetched.isNotEmpty()) {

                    repo.insertArticles(fetched)

                }
            } catch (e: Exception) {

                println("loadHeadlines error: ${e.message}")

            }

            _articles.value = repo.getCachedHeadlines()

            isLoading = false

        }
    }

    fun search(query: String, refresh: Boolean = false) {

        if (isLoading) return
        viewModelScope.launch {

            isLoading = true
            if (refresh) currentPage = 1
            currentQuery = query

            try {

                val fetched = repo.fetchSearch(query, page = currentPage)
                if (fetched.isNotEmpty()) {

                    repo.insertArticles(fetched)

                }
            } catch (_: Exception) {}

            _articles.value = repo.getCachedByQuery(query)

            isLoading = false

        }
    }

    fun loadMore() {
        if (isLoading) return
        viewModelScope.launch {

            isLoading = true
            currentPage++

            if (currentQuery.isNotBlank()) {

                try {

                    val fetched = repo.fetchSearch(currentQuery, page = currentPage)
                    if (fetched.isNotEmpty()) repo.insertArticles(fetched)

                } catch (_: Exception) {}
                _articles.value = repo.getCachedByQuery(currentQuery)
            } else {

                try {

                    val fetched = repo.fetchHeadlines(category = null, page = currentPage)
                    if (fetched.isNotEmpty()) repo.insertArticles(fetched)

                } catch (_: Exception) {}
                _articles.value = repo.getCachedHeadlines()
            }

            isLoading = false

        }
    }
}