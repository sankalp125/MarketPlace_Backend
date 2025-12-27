package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id : String = UUID.randomUUID().toString(),
    val name  : String,
    val email : String,
    val password : String,
    val mobile : String,
    val country : String,
    val state : String,
    val city : String,
    val photoUrl : String,
    val createdAt : LocalDateTime = LocalDateTime.now(),
    val updatedAt : LocalDateTime = LocalDateTime.now()
)
@Serializable
data class UserResponse(
    val id : String,
    val name  : String,
    val email : String,
    val password : String,
    val mobile : String,
    val country : String,
    val state : String,
    val city : String,
    val photoUrl : String,
    val createdAt : String,
    val updatedAt : String
)
