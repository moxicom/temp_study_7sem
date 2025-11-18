package com.aemarkov.lab9.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_API_URL = "api_base_url"
    private const val DEFAULT_API_URL = "http://10.0.2.2:8080/api" // Android emulator localhost
    
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    
    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            val baseUrl = getBaseUrl(context)
            retrofit = createRetrofit(baseUrl)
            apiService = retrofit?.create(ApiService::class.java)
        }
        return apiService!!
    }
    
    fun updateBaseUrl(context: Context, newUrl: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API_URL, newUrl).apply()
        
        // Пересоздаем клиент с новым URL
        retrofit = createRetrofit(newUrl)
        apiService = retrofit?.create(ApiService::class.java)
    }
    
    fun getBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
    }
    
    private fun createRetrofit(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        
        // Убеждаемся, что URL заканчивается на /
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        
        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

