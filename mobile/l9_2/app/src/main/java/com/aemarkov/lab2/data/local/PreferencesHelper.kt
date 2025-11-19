package com.aemarkov.lab2.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.aemarkov.lab2.data.models.Expense
import com.aemarkov.lab2.data.models.Category

class PreferencesHelper(context: Context, private val gson: Gson) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "expense_tracker_prefs"
        private const val KEY_EXPENSES = "expenses"
        private const val KEY_CATEGORIES = "categories"
    }
    
    fun saveExpenses(expenses: List<Expense>) {
        val json = gson.toJson(expenses)
        prefs.edit().putString(KEY_EXPENSES, json).apply()
    }
    
    fun getExpenses(): List<Expense> {
        val json = prefs.getString(KEY_EXPENSES, null) ?: return emptyList()
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun addExpense(expense: Expense) {
        val expenses = getExpenses().toMutableList()
        expenses.add(expense)
        saveExpenses(expenses)
    }
    
    fun saveCategories(categories: List<Category>) {
        val json = gson.toJson(categories)
        prefs.edit().putString(KEY_CATEGORIES, json).apply()
    }
    
    fun getCategories(): List<Category> {
        val json = prefs.getString(KEY_CATEGORIES, null) ?: return emptyList()
        val type = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

