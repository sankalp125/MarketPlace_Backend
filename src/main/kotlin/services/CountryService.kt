package com.example.services

import com.example.models.CountryResponse
import com.example.repository.CountryRepository

class CountryService(private val countryRepository : CountryRepository) {
    fun getAllCountries() : List<CountryResponse>{
        return countryRepository.getAllCountries()
    }
}