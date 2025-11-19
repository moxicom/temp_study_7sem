package com.aemarkov.lab9

import android.app.Application
import android.content.Context
import com.aemarkov.lab9.api.ApiClient
import com.aemarkov.lab9.util.NotificationHelper

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()

        AppData.apiService = ApiClient.getApiService(this)

        AppData.prefs = getSharedPreferences(AppData.PREFS_NAME, Context.MODE_PRIVATE)

        NotificationHelper.createNotificationChannel(this)
    }
}

