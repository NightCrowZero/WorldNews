package com.example.worldnews.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldnews.databinding.FragmentHomeBinding
import com.example.worldnews.ui.adapter.NewsAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _b: FragmentHomeBinding? = null

    private val b get() = _b!!

    private val vm: HomeViewModel by viewModels()

    private lateinit var adapter: NewsAdapter

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = NewsAdapter { article ->
            article.url?.let { url ->
                requireContext().startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(url)
                    )
                )
            }
        }

        b.rvNews.layoutManager = LinearLayoutManager(requireContext())
        b.rvNews.adapter = adapter

        vm.loadHeadlines()

        lifecycleScope.launch {
            vm.articles.collectLatest { list ->
                adapter.submitList(list)
            }
        }

        b.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let { vm.search(it, refresh = true) }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })

        b.btnLoadMore.setOnClickListener {

            vm.loadMore()

        }
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _b = null

    }
}