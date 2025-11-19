package com.aemarkov.lab2.data.models

data class Category(
    val id: Int,
    val name: String,
    val icon: String?
)

data class CategoriesResponse(
    val categories: List<Category>
)

