package com.aemarkov.lab2.data.repository

import com.aemarkov.lab2.data.api.ExpenseApi
import com.aemarkov.lab2.data.local.PreferencesHelper
import com.aemarkov.lab2.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

class ExpenseRepository(
    private val api: ExpenseApi,
    private val preferencesHelper: PreferencesHelper
) {
    
    suspend fun getExpenses(
        category: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Flow<Result<List<Expense>>> = flow {
        try {
            val response = api.getExpenses(category, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                val expenses = response.body()!!.expenses
                preferencesHelper.saveExpenses(expenses)
                emit(Result.success(expenses))
            } else {
                // Fallback to local storage
                val localExpenses = preferencesHelper.getExpenses()
                emit(Result.success(localExpenses))
            }
        } catch (e: Exception) {
            // Fallback to local storage on error
            val localExpenses = preferencesHelper.getExpenses()
            emit(Result.success(localExpenses))
        }
    }
    
    suspend fun createExpense(expense: ExpenseRequest): Flow<Result<Expense>> = flow {
        try {
            val response = api.createExpense(expense)
            if (response.isSuccessful && response.body() != null) {
                val createdExpense = response.body()!!
                preferencesHelper.addExpense(createdExpense)
                emit(Result.success(createdExpense))
            } else {
                // Create locally if API fails
                val localExpense = Expense(
                    id = System.currentTimeMillis().toInt(),
                    amount = expense.amount,
                    category = expense.category,
                    description = expense.description,
                    date = expense.date,
                    createdAt = null,
                    updatedAt = null
                )
                preferencesHelper.addExpense(localExpense)
                emit(Result.success(localExpense))
            }
        } catch (e: Exception) {
            // Create locally on error
            val localExpense = Expense(
                id = System.currentTimeMillis().toInt(),
                amount = expense.amount,
                category = expense.category,
                description = expense.description,
                date = expense.date,
                createdAt = null,
                updatedAt = null
            )
            preferencesHelper.addExpense(localExpense)
            emit(Result.success(localExpense))
        }
    }
    
    suspend fun getCategories(): Flow<Result<List<Category>>> = flow {
        try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val categories = response.body()!!.categories
                preferencesHelper.saveCategories(categories)
                emit(Result.success(categories))
            } else {
                // Fallback to local storage
                val localCategories = preferencesHelper.getCategories()
                emit(Result.success(localCategories))
            }
        } catch (e: Exception) {
            // Fallback to local storage on error
            val localCategories = preferencesHelper.getCategories()
            emit(Result.success(localCategories))
        }
    }
    
    suspend fun getStatistics(
        period: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Flow<Result<Statistics>> = flow {
        try {
            val response = api.getStatistics(period, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                // Calculate from local data
                val expenses = preferencesHelper.getExpenses()
                val statistics = calculateStatistics(expenses, startDate, endDate)
                emit(Result.success(statistics))
            }
        } catch (e: Exception) {
            // Calculate from local data on error
            val expenses = preferencesHelper.getExpenses()
            val statistics = calculateStatistics(expenses, startDate, endDate)
            emit(Result.success(statistics))
        }
    }
    
    private fun calculateStatistics(
        expenses: List<Expense>,
        startDate: String?,
        endDate: String?
    ): Statistics {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        
        val filteredExpenses = expenses.filter { expense ->
            val expenseDate = try {
                dateFormat.parse(expense.date) ?: return@filter false
            } catch (e: Exception) {
                return@filter false
            }
            
            val start = startDate?.let { dateFormat.parse(it) }
            val end = endDate?.let { dateFormat.parse(it) }
            
            (start == null || expenseDate >= start) && (end == null || expenseDate <= end)
        }
        
        val totalAmount = filteredExpenses.sumOf { it.amount }
        val expenseCount = filteredExpenses.size
        
        val byCategory = filteredExpenses
            .groupBy { it.category }
            .map { (category, categoryExpenses) ->
                CategoryStatistics(
                    category = category,
                    amount = categoryExpenses.sumOf { it.amount },
                    count = categoryExpenses.size
                )
            }
        
        val byPeriod = filteredExpenses
            .groupBy { 
                val date = try {
                    dateFormat.parse(it.date)
                } catch (e: Exception) {
                    null
                }
                date?.let { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(it) } ?: "unknown"
            }
            .map { (period, periodExpenses) ->
                PeriodStatistics(
                    period = period,
                    amount = periodExpenses.sumOf { it.amount }
                )
            }
        
        return Statistics(
            totalAmount = totalAmount,
            expenseCount = expenseCount,
            byCategory = byCategory,
            byPeriod = byPeriod
        )
    }
}

