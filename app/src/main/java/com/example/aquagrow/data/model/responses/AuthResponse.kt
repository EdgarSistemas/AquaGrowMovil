package com.example.aquagrow.data.model.responses

import com.example.aquagrow.data.model.domain.User

data class AuthResponse(
    val mensaje: String,
    val token: String,
    val type_token: String,
    val user: User
)