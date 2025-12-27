package com.example.services

import com.example.models.CityResponse
import com.example.repository.CityRepository
import java.util.UUID

class CItyService(private val cityRepository : CityRepository) {
    fun getAllCities(stateId : UUID) : List<CityResponse>{
        return cityRepository.getCities(stateId)
    }
}