package com.example.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object ProductTable : Table("product"){
    val productId = uuid("id")
    val uploadedBy = reference("uploaded_by", UserTable.userId)
    val productName = varchar("name", 100)
    val productCategory = reference("category", CategoriesTable.categoryId)
    val productDescription = varchar("discription", 10000)
    val productPrice = double("price")
    val productEndDate = varchar("till_date", 12)
    val forCountry = reference("for_country", CountriesTable.countryId)
    val forState = reference("for_state", StatesTable.stateId)
    val for_city = reference("for_city", CitiesTable.cityId)
    val productPhoto = varchar("photo_url", 255).nullable()
    val active = bool("active")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(productId)
}