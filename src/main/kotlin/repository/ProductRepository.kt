package com.example.repository

import com.example.DTO.UpdateProductDto
import com.example.models.MyProductsListResponse
import com.example.models.Product
import com.example.models.ProductDetails
import com.example.models.ProductListResponse
import com.example.models.ProductUrl
import com.example.tables.CategoriesTable
import com.example.tables.CitiesTable
import com.example.tables.CountriesTable
import com.example.tables.ProductPicturesTable
import com.example.tables.ProductTable
import com.example.tables.StatesTable
import com.example.tables.UserTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object ProductRepository {
    fun addProduct(product: Product): String = transaction {
        try {
            ProductTable.insert {
                it[productId] = UUID.fromString(product.id)
                it[uploadedBy] = UUID.fromString(product.uploadedBy)
                it[productName] = product.name
                it[productCategory] = UUID.fromString(product.category)
                it[productDescription] = product.discription
                it[productPrice] = product.price
                it[productEndDate] = product.tillDate
                it[forCountry] = UUID.fromString(product.forCountry)
                it[forState] = UUID.fromString(product.forState)
                it[for_city] = UUID.fromString(product.forCity)
                it[productPhoto] = product.pictureUrl
                it[active] = product.active
                it[createdAt] = product.createdAt
                it[updatedAt] = product.updatedAt
            }
            product.id
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    fun addProductUrl(productUrl: ProductUrl) = transaction {
        try {
            ProductPicturesTable.insert {
                it[id] = UUID.fromString(productUrl.id)
                it[prodId] = UUID.fromString(productUrl.prodId)
                it[photoUrl] = productUrl.photoUrl
            }
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    fun deactivateExpiredProduct()  = transaction{
        try {
            val currentDate = LocalDate.now().toString()
            ProductTable.update({ ProductTable.productEndDate less currentDate and ProductTable.active eq Op.TRUE }) {
                it[active] = false
                it[updatedAt] = LocalDateTime.now()
            }
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    private fun mapToSingleProduct(row: ResultRow): ProductListResponse  {
         return ProductListResponse(
            productId = row[ProductTable.productId].toString(),
            productName = row[ProductTable.productName],
            productDesc = row[ProductTable.productDescription],
            productCity = getCity(row[ProductTable.for_city]),
            productCategory = getCategoryName(row[ProductTable.productCategory]),
            productPrice = (row[ProductTable.productPrice]).toBigDecimal().toPlainString(),
            pictureUrl = row[ProductTable.productPhoto].toString()
        )
    }

    fun getProductList(userId: UUID): List<ProductListResponse> = transaction {
        ProductTable
            .select { (ProductTable.active eq Op.TRUE) and (ProductTable.uploadedBy neq userId) }
            .map { mapToSingleProduct(it) }
    }

    private fun getCategoryName(categoryId: UUID): String = transaction {
        CategoriesTable
            .select { CategoriesTable.categoryId eq categoryId }
            .map { it[CategoriesTable.categoryName] }
            .firstOrNull() ?: "Unknown"
    }

    private fun getCity(cityId: UUID): String = transaction {
        CitiesTable.select { CitiesTable.cityId eq cityId }
            .map { it[CitiesTable.cityName] }
            .firstOrNull() ?: "Unknown"
    }

    private fun getCountry(countryId: UUID): String = transaction {
        CountriesTable
            .select { CountriesTable.countryId eq countryId }
            .map { it[CountriesTable.countryName] }
            .firstOrNull() ?: "Unknown"
    }

    private fun getState(stateId: UUID): String = transaction {
        StatesTable
            .select { StatesTable.stateId eq stateId }
            .map { it[StatesTable.stateName] }
            .firstOrNull() ?: "Unknown"
    }

    fun getProductDetails(productId: UUID): ProductDetails = transaction {
        ProductTable
            .select { ProductTable.productId eq productId }
            .map { mapToProductDetails(it) }
            .single()
    }

    private fun mapToProductDetails(row: ResultRow): ProductDetails {
         return ProductDetails(
            name = row[ProductTable.productName],
            category = getCategoryName(row[ProductTable.productCategory]),
            description = row[ProductTable.productDescription],
            price = row[ProductTable.productPrice],
            forCountry = getCountry(row[ProductTable.forCountry]),
            forState = getState(row[ProductTable.forState]),
            forCity = getCity(row[ProductTable.for_city]),
            pictureUrl = row[ProductTable.productPhoto].toString(),
            publisher = getPublisherName(row[ProductTable.uploadedBy]),
            publisherEmail = getPublisherEmail(row[ProductTable.uploadedBy]),
            publisherMobileNumber = getPublisherNumber(row[ProductTable.uploadedBy]),
            otherPictures = getProductPictures(row[ProductTable.productId])
        )
    }

    private fun getPublisherName(userId: UUID): String = transaction {
        UserTable
            .select { UserTable.userId eq userId }
            .map { it[UserTable.userName] }
            .firstOrNull() ?: "Unknown"
    }

    private fun getPublisherEmail(userId: UUID): String = transaction {
        UserTable
            .select { UserTable.userId eq userId }
            .map { it[UserTable.userEmail] }
            .firstOrNull() ?: "Unknown"
    }

    private fun getPublisherNumber(userId: UUID): String = transaction {
        UserTable
            .select { UserTable.userId eq userId }
            .map { it[UserTable.mobileNo] }
            .firstOrNull() ?: "Unknown"
    }

    fun getProductPictures(productid: UUID): List<String> = transaction {
        ProductPicturesTable
            .select { ProductPicturesTable.prodId eq productid }
            .map { it[ProductPicturesTable.photoUrl] }
    }

    fun updateProductDetails(data: UpdateProductDto) = transaction {
        try {
            ProductTable
                .update({ ProductTable.productId eq UUID.fromString(data.productId) }) {
                    it[ProductTable.productPrice] = data.price
                    it[ProductTable.productEndDate] = data.date
                    it[ProductTable.active] = data.status
                    it[ProductTable.updatedAt] = LocalDateTime.now()
                }
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    fun deleteProduct(productId: UUID) = transaction {
        try {
            ProductPicturesTable
                .deleteWhere { ProductPicturesTable.prodId eq productId }
            ProductTable
                .deleteWhere { ProductTable.productId eq productId }
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    fun getProductPicture(productId: UUID): String = transaction {
        try {
            ProductTable
                .select { ProductTable.productId eq productId }
                .map { it[ProductTable.productPhoto] }
                .singleOrNull() ?: "Unknown"
        } catch (e: Exception) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    private fun mapToMyProducts(row : ResultRow) : MyProductsListResponse {
        return MyProductsListResponse(
            productId = row[ProductTable.productId].toString(),
            productName = row[ProductTable.productName],
            productImage = row[ProductTable.productPhoto].toString(),
            productPrice = row[ProductTable.productPrice].toString(),
            productDate = row[ProductTable.productEndDate],
            productStatus = row[ProductTable.active]
        )
    }

    fun getMyProducts(userId : UUID) : List<MyProductsListResponse> = transaction {
        ProductTable
            .select { ProductTable.uploadedBy eq userId }
            .map { mapToMyProducts(it) }
    }
}