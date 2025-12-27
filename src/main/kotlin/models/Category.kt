package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val categoryId: String,
    val categoryName: String
)