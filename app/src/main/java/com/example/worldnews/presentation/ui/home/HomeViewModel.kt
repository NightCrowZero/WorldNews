package com.example.worldnews.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.repository.NewsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel (
    private val repo: NewsRepositoryImpl
) : ViewModel() {

    private val _articles = MutableStateFlow<List<ArticleEntity>>(emptyList())
    val articles: StateFlow<List<ArticleEntity>> get() = _articles

    private var currentPage = 1
    private var isLoading = false

    fun loadHeadlines(refresh: Boolean = false) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            if (refresh) currentPage = 1

            try {
                val fetched = repo.fetchHeadlines(page = currentPage)
                if (fetched.isNotEmpty()) repo.insertArticles(fetched)
            } catch (_: Exception) {}

            _articles.value = repo.getCachedHeadlinesPaged(currentPage, pageSize)
            isLoading = false
        }
    }

    private val pageSize = 20

    fun loadMore() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            currentPage++

            try {
                val fetched = repo.fetchHeadlines(page = currentPage)
                if (fetched.isNotEmpty()) repo.insertArticles(fetched)
            } catch (_: Exception) {  }

            val fetched = repo.fetchHeadlines(category = null, page = currentPage)
            if (fetched.isNotEmpty()) repo.insertArticles(fetched)
            val newArticles = repo.getCachedHeadlinesPaged(page = currentPage)
            _articles.value += newArticles
            //_articles.value = repo.getCachedHeadlinesPaged(currentPage, pageSize)

            isLoading = false
        }
    }


    fun search(query: String, refresh: Boolean = false) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            if (refresh) currentPage = 1

            try {
                val fetched = repo.fetchSearch(query, page = currentPage)
                if (refresh) repo.clearSearchCache(query)
                if (fetched.isNotEmpty()) repo.insertArticles(fetched)
            } catch (_: Exception) {}

            _articles.value = repo.getCachedByQuery(query)
            isLoading = false
        }
    }
}
