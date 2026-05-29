package com.shopdummy.app.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val thumbnail: String,
    val rating: Double
)
