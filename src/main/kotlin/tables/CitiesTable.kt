package com.example.tables

import org.jetbrains.exposed.sql.Table

object CitiesTable : Table("cities") {
    val cityId = uuid("city_id")
    val cityName = varchar("city_name",255)
    val stateId = reference("stateId", StatesTable.stateId)

    override val primaryKey = PrimaryKey(cityId)
}