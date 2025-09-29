package com.example.worldnews.ui.categories

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
import com.example.worldnews.data.NewsRepository
import com.example.worldnews.data.local.AppDatabase
import com.example.worldnews.databinding.FragmentCategoriesBinding
import com.example.worldnews.ui.NewsViewModel
import com.example.worldnews.ui.adapter.NewsAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class CategoriesFragment : Fragment() {
    private var _b: FragmentCategoriesBinding? = null
    private val b get() = _b!!
    private val vm: CategoriesViewModel by viewModels()

    private val repository by lazy {
        val dao = AppDatabase.getInstance(requireContext()).articleDao()
        NewsRepository(dao)
    }

   /* private val viewModel: NewsViewModel by lazy {
        ViewModelProvider(this, NewsViewModelFactory(repository))
            .get(NewsViewModel::class.java)
    }*/

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
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                )
            }
        }
        b.rvNewsCat.layoutManager = LinearLayoutManager(requireContext())
        b.rvNewsCat.adapter = adapter

        val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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


        // Load more button
        b.btnLoadMoreCat.setOnClickListener {
            if (!isLoading) {
                currentPage++
                loadCategory(currentCategory, currentPage, append = true)
            }
        }

        // Початкове завантаження
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
    private val repository: NewsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(repository) as T
            }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*adapter = NewsAdapter { article ->
    article.url?.let { url ->
        requireContext().startActivity(
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        )
    }
}

b.rvNewsCat.layoutManager = LinearLayoutManager(requireContext())
b.rvNewsCat.adapter = adapter

viewLifecycleOwner.lifecycleScope.launch {
    val articles = repository.fetchCategoryViaEverything("business", 1)
    adapter.submitList(articles)
}


// viewModel.loadNews(category = "business")


val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
b.spinnerCategories.adapter = spAdapter

b.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val cat = categories[pos]
        vm.loadCategory(cat, refresh = true)
    }
    override fun onNothingSelected(parent: AdapterView<*>) {}


b.btnLoadMoreCat.setOnClickListener { vm.loadMore() }

viewLifecycleOwner.lifecycleScope.launch {
    vm.articles.collectLatest { list ->
        adapter.submitList(list)
    }
}
}

override fun onDestroyView() {
super.onDestroyView()
_b = null
}
}*/
