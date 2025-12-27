package com.example.module

import com.example.Constants.DB_PASSWORD
import com.example.Constants.DB_USER
import com.example.Constants.JDBC_URL
import com.example.tables.CategoriesTable
import com.example.tables.CitiesTable
import com.example.tables.CountriesTable
import com.example.tables.OtpTable
import com.example.tables.ProductPicturesTable
import com.example.tables.ProductTable
import com.example.tables.StatesTable
import com.example.tables.UserTable
import java.sql.Connection
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val logger = KotlinLogging.logger {}
    private lateinit var database : Database

    fun init(){
        database = Database.connect(
            url = JDBC_URL,
            driver = "org.postgresql.Driver",
            user = DB_USER,
            password = DB_PASSWORD,
            setupConnection = { connection ->
                connection.autoCommit = false
                connection.transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ
            },
        )
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
        transaction {
            createTable()
        }
        logger.info("Database connected and migrations applied successfully.")
    }
    fun closeDatabaseConnection(){
        if (this::database.isInitialized) { // Check if the database is initialized before attempting to close
            TransactionManager.closeAndUnregister(database)
            logger.info("Database connection closed.")
        } else {
            logger.info("Database was not initialized, skipping closure.")
        }
    }
    fun createTable(){
        // create all the tables that are required
        SchemaUtils.create(CountriesTable)
        SchemaUtils.create(StatesTable)
        SchemaUtils.create(CitiesTable)
        SchemaUtils.create(UserTable)
        SchemaUtils.create(OtpTable)
        SchemaUtils.create(CategoriesTable)
        SchemaUtils.create(ProductTable)
        SchemaUtils.create(ProductPicturesTable)
    }
}
