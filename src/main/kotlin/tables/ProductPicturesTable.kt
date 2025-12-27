package com.example.tables

import org.jetbrains.exposed.sql.Table

object ProductPicturesTable : Table("product_pictures") {
    val id = uuid("id")
    val prodId = reference("product_id", ProductTable.productId)
    val photoUrl = varchar("photoUrl", 255)

    override val primaryKey = PrimaryKey(id)
}