package com.example.worldnews.di

import android.content.Context
import androidx.room.Room
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.AppDatabase
import com.example.worldnews.data.remote.NewsApi
import com.example.worldnews.data.remote.RetrofitInstance
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi = RetrofitInstance.api

    @Provides
    @Singleton
    fun provideDatabase(app: Context): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "news_db").build()

    @Provides
    fun provideArticleDao(db: AppDatabase): ArticleDao = db.articleDao()

    @Provides
    @Singleton
    fun provideNewsRepository(api: NewsApi, dao: ArticleDao): NewsRepository =
        NewsRepositoryImpl(api, dao)
}
