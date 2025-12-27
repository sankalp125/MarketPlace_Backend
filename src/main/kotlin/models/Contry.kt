package com.example.models

import kotlinx.serialization.Serializable
@Serializable
data class CountryResponse(
    val countryId : String,
    val countryName : String
)