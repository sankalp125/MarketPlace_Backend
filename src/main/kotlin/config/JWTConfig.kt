package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.Constants
import java.util.Date

object JWTConfig {
    private val accessAlgorithm = Algorithm.HMAC256(Constants.ACCESS_SECRET)
    private val refreshAlgorithm = Algorithm.HMAC256(Constants.REFRESH_SECRET)

    fun generateAccessToken(userId : String): String{
        return JWT.create()
            .withSubject(userId)
            .withIssuer(Constants.ISSUER)
            .withAudience(Constants.AUDIENCE)
            .withExpiresAt(Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_VALIDITY_IN_MS))
            .withClaim("tokenType", "access")
            .sign(accessAlgorithm)
    }

    fun generateRefreshToken(userId : String): String{
        return JWT.create()
            .withSubject(userId)
            .withIssuer(Constants.ISSUER)
            .withAudience(Constants.AUDIENCE)
            .withExpiresAt(Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_VALIDITY_IN_MS))
            .withClaim("tokenType", "refresh")
            .sign(refreshAlgorithm)
    }

    fun accessTokenVerifier(): JWTVerifier{
        return JWT.require(accessAlgorithm)
            .withClaim("tokenType", "access")
            .withIssuer(Constants.ISSUER)
            .withAudience(Constants.AUDIENCE)
            .build()
    }

    fun verifyRefreshToken(token : String) : DecodedJWT{
        val decodedJwt = JWT.require(refreshAlgorithm)
            .withIssuer(Constants.ISSUER)
            .withAudience(Constants.AUDIENCE)
            .build()
            .verify(token)

        val tokenType =decodedJwt.getClaim("tokenType").asString()
        if (tokenType != "refresh"){
            throw IllegalArgumentException("Invalid Token Type")
        }
        return decodedJwt
    }
}