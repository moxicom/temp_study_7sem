package com.aemarkov.lab2.data.api

import com.aemarkov.lab2.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApi {
    
    @GET("expenses")
    suspend fun getExpenses(
        @Query("category") category: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ExpensesResponse>
    
    @POST("expenses")
    suspend fun createExpense(
        @Body expense: ExpenseRequest
    ): Response<Expense>
    
    @GET("expenses/{id}")
    suspend fun getExpenseById(
        @Path("id") id: Int
    ): Response<Expense>
    
    @PUT("expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: Int,
        @Body expense: ExpenseRequest
    ): Response<Expense>
    
    @DELETE("expenses/{id}")
    suspend fun deleteExpense(
        @Path("id") id: Int
    ): Response<Map<String, String>>
    
    @GET("expenses/statistics")
    suspend fun getStatistics(
        @Query("period") period: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<Statistics>
    
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>
}

