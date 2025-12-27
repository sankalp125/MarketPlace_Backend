package com.example.module

import com.example.repository.ProductRepository
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Application.scheduleJobs(){
    // Scheduler for deactivating Expired products
    launch {
        while (true){
            delay(24*60*60*1000L)
            try {
                ProductRepository.deactivateExpiredProduct()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}