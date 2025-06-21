package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.AuthRequest
import com.example.aquagrow.data.model.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login (@Body request : AuthRequest) : Response<AuthResponse>
}