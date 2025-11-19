package com.aemarkov.lab9

import android.content.SharedPreferences
import com.aemarkov.lab9.api.ApiService

object AppData {
    lateinit var apiService: ApiService
    lateinit var prefs: SharedPreferences
    
    // Константы для SharedPreferences
    const val PREFS_NAME = "app_prefs"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_NOTIFICATION_HOUR = "notification_hour"
    const val KEY_NOTIFICATION_MINUTE = "notification_minute"
    const val KEY_BUDGET_LIMIT = "budget_limit"
}

