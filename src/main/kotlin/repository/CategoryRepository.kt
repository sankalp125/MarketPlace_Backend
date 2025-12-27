package com.example.repository

import com.example.models.CategoryResponse
import com.example.tables.CategoriesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CategoryRepository {
    fun getCategories(): List<CategoryResponse> = transaction {
        CategoriesTable.selectAll()
            .map {
                CategoryResponse(
                    categoryId = it[CategoriesTable.categoryId].toString(),
                    categoryName = it[CategoriesTable.categoryName]
                )
            }
    }
}