package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordDto(
    val oldPassword : String,
    val newPassword : String
)

fun validateUpdatePassword(data : UpdatePasswordDto): List<String>{
    val errorList = mutableListOf<String>()
    if (data.oldPassword.isBlank()){
        errorList.add("Your Old Password is Required!")
    }
    if (data.newPassword.isBlank() || data.newPassword.length < 8){
        errorList.add("New Password should be at least 8 characters long!")
    }
    return errorList
}