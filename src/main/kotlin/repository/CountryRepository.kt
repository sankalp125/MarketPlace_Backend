package com.example.repository

import com.example.models.CountryResponse
import com.example.tables.CountriesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CountryRepository {
    fun getAllCountries(): List<CountryResponse> = transaction{
        CountriesTable.selectAll().map{
            CountryResponse(
                countryId = it[CountriesTable.countryId].toString(),
                countryName = it[CountriesTable.countryName]
            )
        }
    }
}