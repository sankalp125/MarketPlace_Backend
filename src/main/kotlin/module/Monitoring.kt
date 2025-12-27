package com.example.module

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.path
import io.ktor.server.response.header
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
        reply { call, callId ->
            call.response.header(HttpHeaders.XRequestId, callId)
        }
    }
}