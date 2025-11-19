package com.aemarkov.lab9

import android.app.Application
import android.content.Context
import com.aemarkov.lab9.api.ApiClient

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация apiService
        AppData.apiService = ApiClient.getApiService(this)
        
        // Инициализация prefs
        AppData.prefs = getSharedPreferences(AppData.PREFS_NAME, Context.MODE_PRIVATE)
    }
}

