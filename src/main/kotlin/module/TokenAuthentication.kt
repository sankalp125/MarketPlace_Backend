package com.example.module

import com.example.config.JWTConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity(){
    install(Authentication){
        jwt("auth-jwt"){
            verifier(JWTConfig.accessTokenVerifier())
            validate { credential ->
                if (credential.payload.getClaim("tokenType").asString() == "access"){
                    JWTPrincipal(credential.payload)
                }else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is invalid or has expired")
            }
        }
    }
}