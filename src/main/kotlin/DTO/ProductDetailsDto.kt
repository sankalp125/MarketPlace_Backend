package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailsDto(
    val productId : String
)

fun validateProductDetailsDto(params : ProductDetailsDto) : List<String>{
    val errorList = mutableListOf<String>()
    if (params.productId.isBlank()){
        errorList.add("Product id is required!")
    }
    return errorList
}