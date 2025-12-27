package com.example.services

import com.example.models.StatesResponse
import com.example.repository.StatesRepository
import java.util.UUID

class StatesService(private val stateRepository : StatesRepository) {
    fun getStates(countryId : UUID) : List<StatesResponse> {
        return stateRepository.getStates(countryId)
    }
}