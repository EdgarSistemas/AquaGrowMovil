package com.example.aquagrow.core.network.services

import com.example.aquagrow.models.requests.LoginRequest
import com.example.aquagrow.models.responses.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login (@Body request : LoginRequest) : AuthResponse
}