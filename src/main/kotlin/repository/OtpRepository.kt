package com.example.repository

import com.example.tables.OtpTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object OtpRepository {
    fun createOtp(email: String, otp: String, expiresAt: LocalDateTime): Boolean = transaction {
        // Delete any existing OTPs for this email
        OtpTable.deleteWhere { OtpTable.email eq email }

        // Create new OTP
        OtpTable.insert {
            it[OtpTable.email] = email
            it[OtpTable.otp] = otp
            it[OtpTable.expiresAt] = expiresAt
        }
        true
    }

    fun verifyOtp(email: String, otp: String): Boolean = transaction {
        val currentTime = LocalDateTime.now()
        val result = OtpTable.select {
            (OtpTable.email eq email) and
            (OtpTable.otp eq otp) and
            (OtpTable.expiresAt greaterEq currentTime)
        }.count() > 0

        // Delete the OTP only if verification was successful
        if (result) {
            OtpTable.deleteWhere { OtpTable.email eq email }
        }

        result
    }

    // Function to delete expired OTPs
    fun deleteExpiredOtps(): Int = transaction {
        val currentTime = LocalDateTime.now()
        OtpTable.deleteWhere { OtpTable.expiresAt less currentTime }
    }

    fun generateOtp(): String {
        return (1000..9999).random().toString()
    }
}
