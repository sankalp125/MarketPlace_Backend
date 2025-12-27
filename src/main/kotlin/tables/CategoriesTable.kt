package com.example.tables

import org.jetbrains.exposed.sql.Table

object CategoriesTable : Table("categories") {
    val categoryId = uuid("id")
    val categoryName = varchar("name", 100)

    override val primaryKey = PrimaryKey(categoryId)
}