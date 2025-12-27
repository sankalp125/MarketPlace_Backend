package com.example.tables

import org.jetbrains.exposed.sql.Table

object CountriesTable : Table("countries") {
    val countryId = uuid("country_id")
    val countryName = varchar("country_Name", 255)

    override val primaryKey = PrimaryKey(countryId)
}