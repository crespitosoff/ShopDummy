package com.shopdummy.app.data.remote.dto

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double?,
    val rating: Double?,
    val stock: Int?,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    val images: List<String>?
)

data class ProductListResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
