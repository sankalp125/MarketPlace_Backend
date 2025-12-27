package com.example.plugins

import com.example.routing.normalRoutes
import com.example.routing.protectedRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting(){
    routing {
        staticFiles("/uploads", File("uploads"))
        normalRoutes()
        protectedRoutes()
    }
}