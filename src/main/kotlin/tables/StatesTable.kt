package com.example.tables

import org.jetbrains.exposed.sql.Table

object StatesTable : Table("states") {
    val stateId = uuid("state_id")
    val stateName = varchar("state_name", 255)
    val countryId = reference("country_id", CountriesTable.countryId)

    override val primaryKey = PrimaryKey(stateId)
}