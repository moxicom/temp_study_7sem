package com.aemarkov.lab9.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Expense(
    val id: Int? = null,
    val amount: Double,
    val category: String,
    val description: String? = null,
    val date: String, // ISO 8601 format
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class ExpenseRequest(
    val amount: Double,
    val category: String,
    val description: String? = null,
    val date: String // ISO 8601 format
)

data class ExpensesResponse(
    val expenses: List<Expense>
)

