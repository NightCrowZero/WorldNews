package com.example.worldnews.data.remote

import android.util.Log
import com.example.worldnews.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://newsapi.org/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()
            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
                .build()
            Log.d("API_TEST", "Key = ${BuildConfig.NEWS_API_KEY}")

            val newRequest = original.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
        .build()

    val api: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}


/*
object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
                .build()

            val newRequest = original.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(client) // додаємо клієнт з interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: NewsApi by lazy { retrofit.create(NewsApi::class.java)  }
}
*/