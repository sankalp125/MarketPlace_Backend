package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name : String,
    val email : String,
    val password : String,
    val mobileNo : String,
    val country : String,
    val state : String,
    val city : String,
    val photoUrl : String? = ""
)
fun validateUser(user : UserDto) : List<String>{
    val errorList = mutableListOf<String>()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    if (user.name.isBlank()){
        errorList.add("Name cannot be empty!")
    }
    if (user.email.isBlank() || !user.email.matches(emailRegex)){
        errorList.add("Invalid Email!")
    }
    if (user.password.isBlank() || user.password.length < 8){
        errorList.add("Password must at least 8 characters long")
    }
    if (user.mobileNo.isBlank() || user.mobileNo.length > 13){
        errorList.add("Invalid mobile number")
    }
    if (user.country.isBlank()){
        errorList.add("Country not found!")
    }
    if (user.state.isBlank()){
        errorList.add("State not found!")
    }
    if (user.city.isBlank()){
        errorList.add("City not found!")
    }

    return errorList
}
