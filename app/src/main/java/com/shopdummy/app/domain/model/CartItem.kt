package com.shopdummy.app.domain.model

data class CartItem(
    val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
)
