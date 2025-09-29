package com.example.worldnews.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.worldnews.R
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.databinding.ItemArticleBinding

class NewsAdapter(
    private val onItemClick: (ArticleEntity) -> Unit
) : ListAdapter<ArticleEntity, NewsAdapter.NewsViewHolder>(DiffCallback()) {

    inner class NewsViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: ArticleEntity) {
            binding.tvTitle.text = article.title ?: "No title"
            binding.tvSource.text = article.sourceName ?: ""
            binding.tvDesc.text = article.description ?: ""

            Glide.with(binding.ivArticle.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image) // опційно, поки завантажується
                .error(R.drawable.error_image) // опційно, якщо помилка
                .into(binding.ivArticle)

            binding.root.setOnClickListener {

                onItemClick(article)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NewsViewHolder(binding)

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        holder.bind(getItem(position))

    }

    class DiffCallback : DiffUtil.ItemCallback<ArticleEntity>() {

        override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity) =
            oldItem == newItem

    }
}
