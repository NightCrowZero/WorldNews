package com.example.worldnews.data

import android.content.Context
import com.example.worldnews.data.local.AppDatabase

object AppDatabaseProvider {
    fun get(context: Context) = AppDatabase.getInstance(context)
}