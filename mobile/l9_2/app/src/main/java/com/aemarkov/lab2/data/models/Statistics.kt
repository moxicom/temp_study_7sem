package com.aemarkov.lab2.data.models

data class Statistics(
    val totalAmount: Double,
    val expenseCount: Int,
    val byCategory: List<CategoryStatistics>,
    val byPeriod: List<PeriodStatistics>
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

