package com.aemarkov.lab9.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String? = null
)

data class CategoriesResponse(
    val categories: List<Category>
)

