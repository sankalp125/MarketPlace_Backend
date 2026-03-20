package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class Product(
    val id : String = UUID.randomUUID().toString(),
    val uploadedBy : String,
    val name : String,
    val category : String,
    val discription : String,
    val price : Double,
    val tillDate : String,
    val forCountry : String,
    val forState : String,
    val forCity : String,
    val pictureUrl : String,
    val active : Boolean,
    val createdAt : LocalDateTime = LocalDateTime.now(),
    val updatedAt : LocalDateTime = LocalDateTime.now()
)

data class ProductUrl(
    val id : String = UUID.randomUUID().toString(),
    val prodId : String,
    val photoUrl : String
)

@Serializable
data class ProductListResponse(
    val productId: String,
    val productName : String,
    val productDesc : String,
    val productCategory : String,
    val productCity : String,
    val productPrice : String,
    val pictureUrl : String
)

@Serializable
data class ProductDetails(
    val name : String,
    val category : String,
    val description : String,
    val price : Double,
    val date : String,
    val status : Boolean,
    val forCountry : String,
    val forState : String,
    val forCity : String,
    val pictureUrl : String,
    val publisher : String,
    val publisherEmail : String,
    val publisherMobileNumber : String,
    val otherPictures : List<String>
)

@Serializable
data class MyProductsListResponse(
    val productId : String,
    val productImage : String,
    val productName : String,
    val productPrice : Double,
    val productDate : String,
    val productStatus : Boolean
)