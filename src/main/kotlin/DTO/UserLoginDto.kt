package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDto(
    val email : String,
    val password : String
)

fun validateCredentials(credentials : UserLoginDto): List<String> {
    val errorList = mutableListOf<String>()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    if (credentials.email.isEmpty() || !credentials.email.matches(emailRegex)){
        errorList.add("Invalid email")
    }
    if (credentials.password.isBlank()){
        errorList.add("Password required")
    }
    return errorList
}