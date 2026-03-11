package com.example

object Constants {
    //staging
    val JDBC_URL = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/jmp_db"
    val DB_USER = System.getenv("DB_USER") ?: "postgres"
    val DB_PASSWORD = System.getenv("DB_PASS") ?: "123456"
    val ACCESS_SECRET = System.getenv("ACCESS_KEY") ?: "access_token_secret_key"
    val REFRESH_SECRET = System.getenv("REFRESH_KEY") ?: "refresh_token_secret_key"
    val ISSUER = System.getenv("JWT_ISSUER") ?: "issuer"
    val AUDIENCE = System.getenv("JWT_AUDIENCE") ?: "audience"
    const val ACCESS_TOKEN_VALIDITY_IN_MS = 1_000 * 60 * 15 // 15 minutes
    const val REFRESH_TOKEN_VALIDITY_IN_MS = 1_000 * 60 * 60 * 24 * 7L // 7 days
    const val PICTURE_URL = "http://172.31.42.84:8080/uploads"
    //LIVE
//    const val PICTURE_URL = "https://jmpwebservices-production.up.railway.app/uploads"
}