package com.example.DTO

import kotlinx.serialization.Serializable

@Serializable
data class AddProductDto(
    val productName : String,
    val category : String,
    val description : String,
    val price : Double,
    val tillDate : String,
    val country : String,
    val state : String,
    val city : String,
    val photoUrl : String,
    val active : Boolean
)

fun validateAddProductDto(data : AddProductDto) : List<String>{
    val errorList = mutableListOf<String>()
    if (data.productName.isEmpty()){
        errorList.add("Product name can not Empty!")
    }
    if (data.category.isEmpty()){
        errorList.add("Product category is required!")
    }
    if (data.price.isNaN()){
        errorList.add("Product price is required!")
    }
    if (data.country.isEmpty()){
        errorList.add("Country is required!")
    }
    if (data.state.isEmpty()){
        errorList.add("State is required!")
    }
    if (data.city.isEmpty()){
        errorList.add("City is required!")
    }
    return errorList
}