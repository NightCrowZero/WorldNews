package com.example.worldnews.presentation.ui.categories

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.data.local.AppDatabase
import com.example.worldnews.data.remote.RetrofitInstance
import com.example.worldnews.data.repository.AppDatabaseProvider
import com.example.worldnews.databinding.FragmentCategoriesBinding
import com.example.worldnews.presentation.viewmodel.NewsViewModel
import com.example.worldnews.presentation.ui.adapter.NewsAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class CategoriesFragment : Fragment() {
    private var _b: FragmentCategoriesBinding? = null
    private val b get() = _b!!
    private val vm: CategoriesViewModel by viewModels()

    private val repository by lazy {
        val dao = AppDatabase.getInstance(requireContext()).articleDao()

        val api = RetrofitInstance.api

        NewsRepositoryImpl(api, dao)
    }

    private lateinit var adapter: NewsAdapter

    private val categories = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")

    private var currentCategory = "business"
    private var currentPage = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCategoriesBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NewsAdapter { article ->
            article.url?.let { url ->
                requireContext().startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            }
        }
        b.rvNewsCat.layoutManager = LinearLayoutManager(requireContext())
        b.rvNewsCat.adapter = adapter

        val spAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, categories)
        spAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        b.spinnerCategories.adapter = spAdapter

        b.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val cat = categories[pos]
                // Лоадимо категорію через новий метод репозиторію
                viewLifecycleOwner.lifecycleScope.launch {
                    val articles = repository.fetchCategoryViaEverything(cat, 1)
                    adapter.submitList(articles)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        b.btnLoadMoreCat.setOnClickListener {
            if (!isLoading) {
                currentPage++
                loadCategory(currentCategory, currentPage, append = true)
            }
        }

        loadCategory(currentCategory, currentPage)
    }

    private fun loadCategory(category: String, page: Int, append: Boolean = false) {
        isLoading = true
        viewLifecycleOwner.lifecycleScope.launch {
            val articles = repository.fetchCategoryViaEverything(category, page)
            if (append) {
                val currentList = adapter.currentList.toMutableList()
                currentList.addAll(articles)
                adapter.submitList(currentList)
            } else {
                adapter.submitList(articles)
            }
            isLoading = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

class NewsViewModelFactory(
    private val repository: NewsRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(repository) as T
            }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}