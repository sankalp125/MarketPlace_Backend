package com.example.repository

import com.example.models.StatesResponse
import com.example.tables.StatesTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object StatesRepository {
    fun getStates(countryId : UUID) : List<StatesResponse> = transaction {
        StatesTable.select { StatesTable.countryId eq countryId }.map {
            StatesResponse(
                stateId = it[StatesTable.stateId].toString(),
                stateName = it[StatesTable.stateName]
            )
        }
    }
}