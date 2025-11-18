package com.aemarkov.lab9.model

data class Statistics(
    val totalAmount: Double,
    val expenseCount: Int,
    val byCategory: List<CategoryStatistics>,
    val byPeriod: List<PeriodStatistics>? = null
)

data class CategoryStatistics(
    val category: String,
    val amount: Double,
    val count: Int
)

data class PeriodStatistics(
    val period: String,
    val amount: Double
)

