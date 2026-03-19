package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class DeletePictureDto(
    val url : String
)

fun validateDeletePictureDto(dto: DeletePictureDto): Boolean {
    return dto.url.isEmpty()
}