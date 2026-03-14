package com.example.repository

import com.example.DTO.UpdateProfileDetailsDto
import com.example.models.User
import com.example.models.UserResponse
import com.example.tables.CitiesTable
import com.example.tables.CountriesTable
import com.example.tables.StatesTable
import com.example.tables.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

object UserRepository {
    fun createUser(user : User) : String = transaction {
        UserTable.insert {
            it[userId] = UUID.fromString(user.id)
            it[userName] = user.name
            it[userEmail] = user.email
            it[password] = user.password
            it[mobileNo] = user.mobile
            it[country] = UUID.fromString(user.country)
            it[state] = UUID.fromString(user.state)
            it[city] = UUID.fromString(user.city)
            it[photoUrl] = user.photoUrl
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
        }
        "User created Successfully"
    }

    fun checkUserExist(email : String) : Boolean = transaction {
        UserTable.select { UserTable.userEmail eq email }.count().toInt() > 0
    }
    private fun mapToUser(row : ResultRow) : UserResponse{
        return UserResponse(
            id = row[UserTable.userId].toString(),
            email = row[UserTable.userEmail],
            name = row[UserTable.userName],
            password = row[UserTable.password],
            mobile = row[UserTable.mobileNo],
            country = getCountryName(row[UserTable.country]),
            state = getState(row[UserTable.state]),
            city = getCity(row[UserTable.city]),
            photoUrl = row[UserTable.photoUrl].toString(),
            createdAt = row[UserTable.createdAt].toString(),
            updatedAt = row[UserTable.updatedAt].toString()
        )
    }
    private fun getCountryName(countryId: UUID): String = transaction {
        CountriesTable
            .select { CountriesTable.countryId eq countryId }
            .map { it[CountriesTable.countryName] }
            .firstOrNull() ?: "Unknown"
    }
    private fun getState(stateId : UUID) : String = transaction{
        StatesTable.select { StatesTable.stateId eq stateId }.map{
            it[StatesTable.stateName]
        }.firstOrNull() ?: "Unknown"
    }
    private fun getCity(cityId : UUID) : String = transaction {
        CitiesTable.select { CitiesTable.cityId eq cityId }
            .map { it[CitiesTable.cityName] }
            .firstOrNull() ?: "Unknown"
    }
    fun findUserByEmail(email: String) : UserResponse? = transaction {
        UserTable
            .select { UserTable.userEmail eq email }
            .map { mapToUser(it) }
            .singleOrNull()
    }
    fun findUserById(userId: UUID) : UserResponse? = transaction {
        UserTable
            .select { UserTable.userId eq userId }
            .map { mapToUser(it) }
            .singleOrNull()
    }

    fun updatePassword(email: String, hashedPassword: String): Boolean = transaction {
        val updatedRows = UserTable.update({ UserTable.userEmail eq email }) {
            it[password] = hashedPassword
            it[updatedAt] = LocalDateTime.now()
        }
        updatedRows > 0
    }
    fun updateProfileDetails(userId : UUID, profileData : UpdateProfileDetailsDto) = transaction{
        try{
            UserTable.update({ UserTable.userId eq userId}){
                it[userName] = profileData.name
                it[userEmail] = profileData.email
                it[mobileNo] = profileData.mobileNo
                it[country] = UUID.fromString(profileData.country)
                it[state] = UUID.fromString(profileData.state)
                it[city] = UUID.fromString(profileData.city)
                it[updatedAt] = LocalDateTime.now()
            }
        }catch (e : Exception){
            throw Exception("${e.localizedMessage}")
        }
    }
    fun updateProfilePicture(userId : UUID, url : String)  = transaction{
        try{
            UserTable.update( { UserTable.userId eq userId }){
                it[photoUrl] = url
                it[updatedAt] = LocalDateTime.now()
            }
        }catch (e : Exception){
            throw Exception(e.localizedMessage)
        }
    }
    fun getProfilePicture(userId : UUID) : String = transaction{
        try {
            UserTable
                .select { UserTable.userId eq userId }
                .map { it[UserTable.photoUrl] }
                .firstOrNull() ?: "Unknown"
        }catch (e : Exception){
            throw Exception(e.localizedMessage)
        }
    }
}
