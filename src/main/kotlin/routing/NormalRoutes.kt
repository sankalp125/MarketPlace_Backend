package com.example.routing

import com.example.DTO.*
import com.example.config.FileHandler
import com.example.config.JWTConfig
import com.example.models.ErrorResponse
import com.example.models.LoginResponse
import com.example.models.User
import com.example.repository.CategoryRepository
import com.example.repository.CityRepository
import com.example.repository.CountryRepository
import com.example.repository.StatesRepository
import com.example.repository.UserRepository
import com.example.services.CItyService
import com.example.services.CountryService
import com.example.services.StatesService
import com.example.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.mindrot.jbcrypt.BCrypt
import java.io.InputStream
import java.util.UUID

fun Routing.normalRoutes(){
    route("/api/v1/normal"){
        get("/countries"){
            try{
                val countries = CountryService(CountryRepository).getAllCountries()
                if(countries.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, countries)
                }else{
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No data found!"))
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        get("/states/{countryId}"){
            val countryId = call.parameters["countryId"]
            try {
                if (countryId == null){
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Missing parameter country"))
                    return@get
                }else{
                    val id  = UUID.fromString(countryId)
                    val states = StatesService(StatesRepository).getStates(id)
                    if (states.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, states)
                    }else{
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "No data found!"))
                    }
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        get("/cities/{stateId}"){
            val stateId = call.parameters["stateId"]
            try{
                if (stateId == null){
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Missing parameter State"))
                    return@get
                }else{
                    val id = UUID.fromString(stateId)
                    val cities = CItyService(CityRepository).getAllCities(id)
                    if (cities.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, cities)
                    }else{
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "No data found!"))
                    }
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }
        post("/register") {
            val multipart = call.receiveMultipart()
            val formFields = mutableMapOf<String, String>()
            var profilePhotoUrl  = ""
            var uploadedPhotoStream : InputStream? = null
            var uploadedPhotoName : String? = null
            try{
                multipart.forEachPart { part ->
                    when(part){
                        is PartData.FormItem ->{
                            formFields[part.name ?: ""] = part.value
                        }
                        is PartData.FileItem -> {
                            if (part.name == "photo" && part.originalFileName!= null){
                                uploadedPhotoName = part.originalFileName
                                uploadedPhotoStream = part.streamProvider().readBytes().inputStream()
                            }
                        }
                        else -> part.dispose()
                    }
                    part.dispose()
                }
                val userParams = UserDto(
                    name = formFields["name"] ?: "",
                    email = formFields["email"] ?: "",
                    password = formFields["password"] ?: "",
                    mobileNo = formFields["mobileNo"] ?: "",
                    country = formFields["country"] ?: "",
                    state = formFields["state"] ?: "",
                    city = formFields["city"] ?: "",
                    photoUrl = ""
                )
                val validationList = validateUser(userParams)
                if (validationList.isNotEmpty()){
                    return@post call.respond(HttpStatusCode.BadRequest, validationList)
                }
                val fileService = FileHandler
                if (uploadedPhotoStream != null && uploadedPhotoName != null){
                    try {
                        profilePhotoUrl = fileService.uploadSingleFile(uploadedPhotoStream, uploadedPhotoName!!)
                    }catch (e : Exception){
                        return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Photo upload failed ${e.localizedMessage}"))
                    }
                }
                val hashedPassword = BCrypt.hashpw(userParams.password, BCrypt.gensalt())
                val user = User(
                    name = userParams.name,
                    email = userParams.email,
                    password = hashedPassword,
                    mobile = userParams.mobileNo,
                    country = userParams.country,
                    state = userParams.state,
                    city = userParams.city,
                    photoUrl = profilePhotoUrl
                )
                val userService = UserService(UserRepository)
                if (!userService.checkUserExist(user.email)){
                    val result = userService.createUser(user)
                    if (result == "User created Successfully"){
                        call.respond(HttpStatusCode.Created, mapOf("message" to result))
                    }else{
                        fileService.deleteFileByUrl(profilePhotoUrl)
                        call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "Some error occurred"))
                    }
                }else{
                    fileService.deleteFileByUrl(profilePhotoUrl)
                    call.respond(HttpStatusCode.Conflict, ErrorResponse(error = "User already exist"))
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = e.localizedMessage))
            }
        }

        post("/login"){
            val loginParams = call.receive<UserLoginDto>()
            val errorList = validateCredentials(loginParams)
            if (errorList.isEmpty()){
                val user = UserRepository.findUserByEmail(loginParams.email)
                if (user != null){
                    val isValidUser = UserService(UserRepository).isPasswordCorrect(user, loginParams.password)
                    if (isValidUser){
                        val accessToken = JWTConfig.generateAccessToken(user.id)
                        val refreshToken = JWTConfig.generateRefreshToken(user.id)
                        call.respond(HttpStatusCode.OK, LoginResponse(accessToken, refreshToken))
                    }else{
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Incorrect Password!"))
                    }
                }else{
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "User not found!"))
                }
            }else{
                call.respond(HttpStatusCode.BadRequest, errorList)
            }
        }

        post("/request-password-reset") {
            try {
                val resetRequest = call.receive<PasswordResetRequestDto>()
                val errorList = validatePasswordResetRequest(resetRequest)

                if (errorList.isEmpty()) {
                    val userService = UserService(UserRepository)
                    val result = userService.requestPasswordReset(resetRequest.email)
                    if (result) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "OTP sent to your email"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "User not found"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, errorList)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        post("/reset-password") {
            try {
                val resetVerify = call.receive<PasswordResetVerifyDto>()
                val errorList = validatePasswordResetVerify(resetVerify)

                if (errorList.isEmpty()) {
                    val userService = UserService(UserRepository)
                    val result = userService.verifyOtpAndResetPassword(
                        resetVerify.email,
                        resetVerify.otp,
                        resetVerify.newPassword
                    )

                    if (result) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Password reset successful"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = "Invalid OTP or OTP expired"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, errorList)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        post("/resend-otp") {
            try {
                val resetRequest = call.receive<PasswordResetRequestDto>()
                val errorList = validatePasswordResetRequest(resetRequest)
                if (errorList.isEmpty()) {
                    val userService = UserService(UserRepository)
                    val result = userService.requestPasswordReset(resetRequest.email)
                    if (result) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "OTP resent to your email"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "User not found"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, errorList)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        post("/refresh-token") {
            try {
                val refreshRequest = call.receive<TokenRefreshRequestDto>()
                val errorList = validateTokenRefreshRequest(refreshRequest)

                if (errorList.isEmpty()) {
                    try {
                        // Verify the refresh token
                        val decodedJWT = JWTConfig.verifyRefreshToken(refreshRequest.refreshToken)
                        val userId = decodedJWT.subject

                        // Generate new tokens
                        val newAccessToken = JWTConfig.generateAccessToken(userId)
                        val newRefreshToken = JWTConfig.generateRefreshToken(userId)

                        // Return the new tokens
                        call.respond(HttpStatusCode.OK, TokenRefreshResponseDto(
                            accessToken = newAccessToken,
                            refreshToken = newRefreshToken
                        ))
                    } catch (_: Exception) {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Invalid or expired refresh token"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, errorList)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }

        get("/get_categories") {
            try {
                val categories = CategoryRepository.getCategories()
                if (categories.isNotEmpty()){
                    call.respond(HttpStatusCode.OK, categories)
                }else{
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "No data found!"))
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "${e.localizedMessage}"))
            }
        }
    }
    
    // message ques, web Rtc , socket, web assembly
}
