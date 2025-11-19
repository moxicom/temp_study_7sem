package com.aemarkov.lab2.data.models

import com.google.gson.annotations.SerializedName

data class Expense(
    val id: Int,
    val amount: Double,
    val category: String,
    val description: String?,
    val date: String,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class ExpenseRequest(
    val amount: Double,
    val category: String,
    val description: String?,
    val date: String
)

