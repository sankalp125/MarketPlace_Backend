package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductDto(
    val productId : String,
    val price : String,
    val date : String,
    val status : String
)

fun validateUpdateProductDto(params : UpdateProductDto) : List<String> {
    val errorList = mutableListOf<String>()
    if (params.productId.isBlank()){
        errorList.add("Product id is required!")
    }
    if (params.date.isBlank()){
        errorList.add("Date is required!")
    }
    if (params.price.isBlank()){
        errorList.add("Price is required!")
    }
    return errorList
}