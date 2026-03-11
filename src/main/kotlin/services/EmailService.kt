package com.example.services

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import java.sql.DriverManager.println
import java.util.concurrent.Executors

class EmailService {
    companion object {
        private val emailExecutor = Executors.newFixedThreadPool(5)
        
        fun sendOtpEmail(recipientEmail: String, otp: String) {
            emailExecutor.submit {
                try {
                    val email = SimpleEmail()
                    // Configure these values from your application.yaml or environment variables
                    email.hostName = "smtp.gmail.com" // Replace it with your SMTP server
                    email.setSmtpPort(587) // Replace it with your SMTP port
                    email.setAuthenticator(DefaultAuthenticator("sankalpjagran111@gmail.com", "ptps jexy elxo ohlj")) // Replace it with your email credentials
                    email.isStartTLSEnabled = true
                    email.isSSLOnConnect = true
                    email.setFrom("sankalpjagran111@gmail.com") // Replace it with your email
                    email.subject = "Password Reset OTP"
                    email.setMsg("Your OTP for password reset is: $otp. This OTP will expire in 10 minutes.")
                    email.addTo(recipientEmail)
                    email.send()
                } catch (e: Exception) {
                    println("Failed to send email: ${e.message}")
                }
            }
        }
    }
}