package com.shopdummy.app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int = 60
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val token: String
)
