package com.aemarkov.lab2.data.models

data class ErrorResponse(
    val message: String,
    val code: Int?,
    val details: String?
)

