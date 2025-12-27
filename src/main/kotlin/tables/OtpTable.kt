package com.example.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*

object OtpTable : Table("otp") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255)
    val otp = varchar("otp", 6)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}