package com.shopdummy.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Ítem de Carrito para la base de datos Room.
 */
@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
)
