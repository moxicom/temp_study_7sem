package com.aemarkov.lab9

import android.content.SharedPreferences
import com.aemarkov.lab9.api.ApiService

object AppData {
    lateinit var apiService: ApiService
    lateinit var prefs: SharedPreferences
    
    // Константы для SharedPreferences
    const val PREFS_NAME = "app_prefs"
    const val KEY_BUDGET_LIMIT = "budget_limit"
}

