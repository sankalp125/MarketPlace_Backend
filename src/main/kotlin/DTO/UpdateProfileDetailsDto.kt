package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileDetailsDto(
    val name : String,
    val email : String,
    val mobileNo : String,
    val country : String,
    val state : String,
    val city : String
)

fun validateProfileData(data : UpdateProfileDetailsDto) : List<String> {
    val errorList = mutableListOf<String>()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    if (data.name.isBlank()){
        errorList.add("Name cannot be empty!")
    }
    if (data.email.isBlank() || !data.email.matches(emailRegex)){
        errorList.add("Invalid Email!")
    }
    if (data.mobileNo.isBlank() || data.mobileNo.length > 13){
        errorList.add("Invalid mobile number")
    }
    if (data.country.isBlank()){
        errorList.add("Country not found!")
    }
    if (data.state.isBlank()){
        errorList.add("State not found!")
    }
    if (data.city.isBlank()){
        errorList.add("City not found!")
    }

    return errorList
}