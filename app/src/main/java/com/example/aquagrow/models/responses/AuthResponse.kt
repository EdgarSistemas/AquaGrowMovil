package com.example.aquagrow.models.responses

import com.example.aquagrow.models.domain.User

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String, // Token único JWT
    val user: User
)