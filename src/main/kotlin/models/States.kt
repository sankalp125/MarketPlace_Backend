package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class StatesResponse(
    val stateId : String,
    val stateName : String
)