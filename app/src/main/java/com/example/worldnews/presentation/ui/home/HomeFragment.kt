package com.example.worldnews.presentation.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldnews.data.local.AppDatabase
import com.example.worldnews.data.remote.RetrofitInstance
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.databinding.FragmentHomeBinding
import com.example.worldnews.presentation.ui.adapter.NewsAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!

    private val repository by lazy {
        val dao = AppDatabase.getInstance(requireContext()).articleDao()
        NewsRepositoryImpl(RetrofitInstance.api, dao)
    }

    private val vm: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
        }
    }

    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupSearchView()
        setupLoadMoreButton()

        vm.loadHeadlines()

        viewLifecycleOwner.lifecycleScope.launch {
            vm.articles.collectLatest { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter { article ->
            article.url?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        b.rvNews.layoutManager = LinearLayoutManager(requireContext())
        b.rvNews.adapter = adapter
    }

    private fun setupSearchView() {
        b.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { vm.search(it, refresh = true) }
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    private fun setupLoadMoreButton() {
        b.btnLoadMore.setOnClickListener {
            vm.loadMore()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
