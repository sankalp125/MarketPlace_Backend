package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CityResponse(
    val cityId : String,
    val cityName : String
)