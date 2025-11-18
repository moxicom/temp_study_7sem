package com.aemarkov.lab9.api

import com.aemarkov.lab9.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @GET("expenses")
    suspend fun getExpenses(
        @Query("category") category: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ExpensesResponse>
    
    @GET("expenses/{id}")
    suspend fun getExpense(@Path("id") id: Int): Response<Expense>
    
    @POST("expenses")
    suspend fun createExpense(@Body expense: ExpenseRequest): Response<Expense>
    
    @PUT("expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: Int,
        @Body expense: ExpenseRequest
    ): Response<Expense>
    
    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Int): Response<Unit>
    
    @GET("expenses/statistics")
    suspend fun getStatistics(
        @Query("period") period: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<Statistics>
    
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>
}

