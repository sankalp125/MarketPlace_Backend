package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshRequestDto(
    val refreshToken: String
)

@Serializable
data class TokenRefreshResponseDto(
    val accessToken: String,
    val refreshToken: String
)

fun validateTokenRefreshRequest(request: TokenRefreshRequestDto): List<String> {
    val errorList = mutableListOf<String>()
    if (request.refreshToken.isBlank()) {
        errorList.add("Refresh token is required")
    }
    return errorList
}