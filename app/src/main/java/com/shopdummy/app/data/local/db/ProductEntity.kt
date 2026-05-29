package com.shopdummy.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Producto para la base de datos Room (caché local).
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String,
    val category: String,
    val rating: Double
)
