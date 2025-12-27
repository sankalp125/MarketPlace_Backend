package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequestDto(
    val email: String
)

@Serializable
data class PasswordResetVerifyDto(
    val email: String,
    val otp: String,
    val newPassword: String
)

fun validatePasswordResetRequest(request: PasswordResetRequestDto): List<String> {
    val errorList = mutableListOf<String>()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    if (request.email.isEmpty() || !request.email.matches(emailRegex)) {
        errorList.add("Invalid email")
    }
    return errorList
}

fun validatePasswordResetVerify(verify: PasswordResetVerifyDto): List<String> {
    val errorList = mutableListOf<String>()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    if (verify.email.isEmpty() || !verify.email.matches(emailRegex)) {
        errorList.add("Invalid email")
    }
    if (verify.otp.isEmpty() || verify.otp.length != 4 || !verify.otp.all { it.isDigit() }) {
        errorList.add("Invalid OTP")
    }
    if (verify.newPassword.length < 6) {
        errorList.add("Password must be at least 6 characters long")
    }
    return errorList
}