package com.example.routing

import com.example.DTO.AddProductDto
import com.example.DTO.DeletePictureDto
import com.example.DTO.ProductDetailsDto
import com.example.DTO.UpdatePasswordDto
import com.example.DTO.UpdateProductDto
import com.example.DTO.UpdateProfileDetailsDto
import com.example.DTO.validateAddProductDto
import com.example.DTO.validateDeletePictureDto
import com.example.DTO.validateProductDetailsDto
import com.example.DTO.validateProfileData
import com.example.DTO.validateUpdatePassword
import com.example.DTO.validateUpdateProductDto
import com.example.config.FileHandler
import com.example.models.ErrorResponse
import com.example.models.Product
import com.example.models.ProductUrl
import com.example.repository.ProductRepository
import com.example.repository.UserRepository
import com.example.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.io.InputStream
import java.util.UUID

fun Routing.protectedRoutes() {
    route("/api/v1/protected") {
        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Invalid Token: Missing user ID"))
                    return@get
                }

                try {
                    val user = UserRepository.findUserById(UUID.fromString(userId))
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "User not found"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.message}"))
                }
            }
            get("/my-products") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Invalid Token: Missing user ID"))
                    return@get
                }
                try {
                    val myProducts = ProductRepository.getMyProducts(userId = UUID.fromString(userId))
                    if (myProducts.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "No products found"))
                    } else {
                        call.respond(HttpStatusCode.OK, myProducts)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "Error: ${e.message}"))
                }
            }
            get("/profile-picture") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Invalid Token: Missing user ID"))
                    return@get
                }
                try {
                    val photoUrl = UserRepository.getProfilePicture(UUID.fromString(userId))
                    call.respond(HttpStatusCode.OK, mapOf("url" to photoUrl))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            put("/update-profile-details") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@put call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                val params = call.receive<UpdateProfileDetailsDto>()
                val errorList = validateProfileData(params)
                if (errorList.isNotEmpty()) {
                    return@put call.respond(HttpStatusCode.BadRequest, errorList)
                }
                try {
                    UserRepository.updateProfileDetails(UUID.fromString(userId), params)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Profile details updated successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            put("update-profile-picture") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@put call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid token")
                )
                val multipart = call.receiveMultipart()
                var profilePictureName: String? = null
                var profilePictureStream: InputStream? = null
                var profilePictureUrl = ""
                try {
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                if (part.name == "photo" && part.originalFileName != null) {
                                    profilePictureName = part.originalFileName
                                    profilePictureStream = part.streamProvider().readBytes().inputStream()
                                }
                            }

                            else -> {
                                part.dispose()
                            }
                        }
                        part.dispose()
                    }
                    val fileService = FileHandler
                    if (profilePictureStream != null && profilePictureName != null) {
                        try {
                            profilePictureUrl = fileService.uploadSingleFile(profilePictureStream, profilePictureName!!)
                        } catch (e: Exception) {
                            return@put call.respond(
                                HttpStatusCode.BadRequest,
                                ErrorResponse(error = e.localizedMessage)
                            )
                        }
                    }
                    val user = UserRepository.findUserById(UUID.fromString(userId))
                    if (user != null) {
                        val oldUrl = user.photoUrl
                        if (oldUrl != "") {
                            try {
                                fileService.deleteFileByUrl(oldUrl)
                            } catch (e: Exception) {
                                return@put call.respond(
                                    HttpStatusCode.InternalServerError,
                                    ErrorResponse(error = e.localizedMessage)
                                )
                            }
                        } else {
                            try {
                                UserRepository.updateProfilePicture(UUID.fromString(userId), profilePictureUrl)
                                call.respond(
                                    HttpStatusCode.OK,
                                    mapOf("message" to "Profile picture updated successfully")
                                )
                            } catch (e: Exception) {
                                return@put call.respond(
                                    HttpStatusCode.InternalServerError,
                                    ErrorResponse(error = e.localizedMessage)
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            put("/update-password") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@put call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                try {
                    val updatePasswordParams = call.receive<UpdatePasswordDto>()
                    val errorList = validateUpdatePassword(updatePasswordParams)
                    if (errorList.isEmpty()) {
                        return@put call.respond(HttpStatusCode.BadRequest, errorList)
                    }
                    val user = UserRepository.findUserById(UUID.fromString(userId))
                    val oldPasswordCheck =
                        UserService(UserRepository).isPasswordCorrect(user!!, updatePasswordParams.oldPassword)
                    if (!oldPasswordCheck) {
                        return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Incorrect Old Password!")
                        )
                    }
                    val passwordUpdated = UserRepository.updatePassword(user.email, updatePasswordParams.newPassword)
                    if (passwordUpdated) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Password updated successfully!"))
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(error = "Some error occurred while updating password!")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            post("/add-product") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@post call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token!")
                )
                val multipart = call.receiveMultipart()
                var pictureStream: InputStream? = null
                var pictureName: String? = null
                var pictureUrl = ""
                val formField = mutableMapOf<String, String>()
                val fileFields = mutableListOf<Pair<InputStream, String>>()
                try {
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                formField[part.name ?: ""] = part.value
                            }

                            is PartData.FileItem -> {
                                if (part.name == "picture" && part.originalFileName != null) {
                                    pictureStream = part.streamProvider().readBytes().inputStream()
                                    pictureName = part.originalFileName
                                } else {
                                    if (part.originalFileName != null) {
                                        fileFields.add(
                                            part.streamProvider().readBytes().inputStream() to part.originalFileName!!
                                        )
                                    }
                                }
                            }

                            else -> part.dispose()
                        }
                        part.dispose()
                    }
                    val fileService = FileHandler
                    if (pictureStream != null && pictureName != null) {
                        try {
                            pictureUrl = fileService.uploadSingleFile(pictureStream, pictureName!!)
                        } catch (e: Exception) {
                            return@post call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse(error = e.localizedMessage)
                            )
                        }
                    }
                    val prodParams = AddProductDto(
                        productName = formField["productName"] ?: "",
                        category = formField["category"] ?: "",
                        description = formField["description"] ?: "",
                        price = formField["price"]?.toDouble() ?: 0.0,
                        tillDate = formField["tillDate"] ?: "",
                        country = formField["country"] ?: "",
                        state = formField["state"] ?: "",
                        city = formField["city"] ?: "",
                        photoUrl = pictureUrl,
                        active = formField["active"]?.toBoolean() ?: true,
                    )
                    val errorList = validateAddProductDto(prodParams)
                    if (errorList.isNotEmpty()) {
                        fileService.deleteFileByUrl(pictureUrl)
                        return@post call.respond(HttpStatusCode.BadRequest, errorList)
                    }
                    val product = Product(
                        uploadedBy = userId,
                        name = prodParams.productName,
                        category = prodParams.category,
                        discription = prodParams.description,
                        price = prodParams.price,
                        tillDate = prodParams.tillDate,
                        forCountry = prodParams.country,
                        forState = prodParams.state,
                        forCity = prodParams.city,
                        pictureUrl = prodParams.photoUrl,
                        active = prodParams.active
                    )
                    var prodId = ""
                    try {
                        prodId = ProductRepository.addProduct(product)
                    } catch (e: Exception) {
                        fileService.deleteFileByUrl(pictureUrl)
                        return@post call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
                    }
                    if (prodId != "") {
                        try {
                            val urls = fileService.uploadMultipleFile(fileFields)
                            if (urls.isNotEmpty()) {
                                try {
                                    urls.forEach { url ->
                                        val prodUrl = ProductUrl(
                                            prodId = prodId,
                                            photoUrl = url
                                        )
                                        ProductRepository.addProductUrl(prodUrl)
                                    }
                                    call.respond(
                                        HttpStatusCode.Created,
                                        mapOf("message" to "Product added successfully")
                                    )
                                } catch (e: Exception) {
                                    return@post call.respond(
                                        HttpStatusCode.Accepted,
                                        mapOf("message" to "Product added successfully but some error : ${e.localizedMessage} occurred in uploading images try to upload them by edit product!")
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            return@post call.respond(
                                HttpStatusCode.Accepted,
                                mapOf("message" to "Product added successfully but some error : ${e.localizedMessage} occurred in uploading images try to upload them by edit product!")
                            )
                        }
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            get("/product-list") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@get call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                try {
                    val products = ProductRepository.getProductList(UUID.fromString(userId))
                    call.respond(HttpStatusCode.OK, products)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            get("/product-details/{product-id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
                val productId = call.parameters["product-id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(error = "Product id can not be empty!")
                )
                try {
                    val productDetails = ProductRepository.getProductDetails(UUID.fromString(productId))
                    call.respond(HttpStatusCode.OK, productDetails)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }
            put("/update-product") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@put call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                val params = call.receive<UpdateProductDto>()
                val errors = validateUpdateProductDto(params)
                if (errors.isNotEmpty()) {
                    return@put call.respond(HttpStatusCode.BadRequest, errors)
                }
                try {
                    ProductRepository.updateProductDetails(params)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Product updated successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }
            put("/add-product-picture") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@put call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                val multipart = call.receiveMultipart()
                var productId = ""
                val fileFields = mutableListOf<Pair<InputStream, String>>()
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "productId" && part.value != "") {
                                productId = part.value
                            }
                        }

                        is PartData.FileItem -> {
                            if (part.originalFileName != null) {
                                fileFields.add(
                                    part.streamProvider().readBytes().inputStream() to part.originalFileName!!
                                )
                            }
                        }

                        else -> part.dispose()
                    }
                    part.dispose()
                }
                if (productId == "" || fileFields.isEmpty()) {
                    return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Parameter missing!"))
                }
                try {
                    val fileService = FileHandler
                    val urls = fileService.uploadMultipleFile(fileFields)
                    urls.forEach { url ->
                        val prodUrl = ProductUrl(
                            prodId = productId,
                            photoUrl = url
                        )
                        ProductRepository.addProductUrl(prodUrl)
                    }
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Pictures added successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }

            delete("/delete-product") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@delete call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token!")
                )
                try {
                    val params = call.receive<ProductDetailsDto>()
                    val errors = validateProductDetailsDto(params)
                    if (errors.isNotEmpty()) {
                        return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Parameter missing!")
                        )
                    }
                    val urls = ProductRepository.getProductPictures(UUID.fromString(params.productId))
                    val fileService = FileHandler
                    urls.forEach { url ->
                        fileService.deleteFileByUrl(url)
                        ProductRepository.deleteProductPicture(url)
                    }
                    val photoUrl = ProductRepository.getProductPicture(UUID.fromString(params.productId))
                    fileService.deleteFileByUrl(photoUrl)
                    ProductRepository.deleteProduct(UUID.fromString(params.productId))
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Product deleted successfully"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }
            delete("/delete-picture") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: return@delete call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(error = "Invalid Token")
                )
                try {
                    val params = call.receive<DeletePictureDto>()
                    if (validateDeletePictureDto(params)) {
                        return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(error = "Parameter missing!")
                        )
                    } else {
                        val fileService = FileHandler
                        fileService.deleteFileByUrl(params.url)
                        ProductRepository.deleteProductPicture(params.url)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Picture deleted successfully"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
                }
            }
        }
    }
}
