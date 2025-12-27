package com.example.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*

object UserTable : Table("user") {
    val userId = uuid("user_id")
    val userName = varchar("name", 100)
    val userEmail = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val mobileNo = varchar("mobile_number", 13)
    val country = reference("country_id", CountriesTable.countryId)
    val state = reference("state_id", StatesTable.stateId)
    val city = reference("city_id", CitiesTable.cityId)
    val photoUrl = varchar("photo_url", 255).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(userId)
}