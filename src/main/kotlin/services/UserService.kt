package com.example.services

import com.example.models.User
import com.example.models.UserResponse
import com.example.repository.OtpRepository
import com.example.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

class UserService(private val userRepository : UserRepository) {
    fun createUser(user : User) : String {
        return userRepository.createUser(user)
    }
    fun checkUserExist(email : String): Boolean{
        return userRepository.checkUserExist(email)
    }
    fun isPasswordCorrect(user: UserResponse, enteredPassword : String) : Boolean{
        val password = user.password
        val isPasswordCorrect = BCrypt.checkpw(enteredPassword, password)
        return isPasswordCorrect
    }

    fun requestPasswordReset(email: String): Boolean {
        // Check if user exists
        if (!userRepository.checkUserExist(email)) {
            return false
        }

        // Generate OTP
        val otp = OtpRepository.generateOtp()

        // Set expiration time (10 minutes from now)
        val expiresAt = LocalDateTime.now().plusMinutes(10)

        // Store OTP in a database
        val otpCreated = OtpRepository.createOtp(email, otp, expiresAt)

        if (otpCreated) {
            // Send OTP via email
            EmailService.sendOtpEmail(email, otp)
            return true
        }

        return false
    }

    fun verifyOtpAndResetPassword(email: String, otp: String, newPassword: String): Boolean {
        // Delete expired OTPs before verification
        OtpRepository.deleteExpiredOtps()

        // Verify OTP
        val isOtpValid = OtpRepository.verifyOtp(email, otp)

        if (isOtpValid) {
            // Get user
            val user = userRepository.findUserByEmail(email) ?: return false

            // Hash new password
            val hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())

            // Update password in database
            return updateUserPassword(email, hashedPassword)
        }

        return false
    }

    private fun updateUserPassword(email: String, hashedPassword: String): Boolean {
        return userRepository.updatePassword(email, hashedPassword)
    }
}
