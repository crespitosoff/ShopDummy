package com.shopdummy.app.domain.model

/**
 * Modelo de dominio para el Usuario autenticado.
 */
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val token: String
)
