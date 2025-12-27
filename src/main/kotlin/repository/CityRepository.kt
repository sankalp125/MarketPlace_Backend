package com.example.repository

import com.example.models.CityResponse
import com.example.tables.CitiesTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CityRepository {
    fun getCities(stateId : UUID) : List<CityResponse> = transaction {
        CitiesTable.select { CitiesTable.stateId eq stateId }.map {
            CityResponse(
                cityId = it[CitiesTable.cityId].toString(),
                cityName = it[CitiesTable.cityName]
            )
        }
    }
}