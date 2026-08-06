package com.example.module

import com.example.repository.ProductRepository
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun Application.scheduleJobs(){
    // Scheduler for deactivating Expired products
    launch {
        while (true){
            delay((24*60*60*1000L).milliseconds)
            try {
                ProductRepository.deactivateExpiredProduct()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}