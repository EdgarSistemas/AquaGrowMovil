package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.model.requests.AuthRequest
import com.example.aquagrow.data.model.responses.AuthResponse
import com.example.aquagrow.data.remote.api.ApiClient
import com.example.aquagrow.data.remote.api.services.AuthService
import retrofit2.HttpException

class AuthRepository(
    private val authService: AuthService = ApiClient.authService,
    private val sessionManager: SessionManager = SessionManager
) {
    suspend fun login(username: String, password: String): AuthResponse {
        Log.d("AuthRepo", "Iniciando login...")
        return try {
            val request = AuthRequest(username, password)
            val response = authService.login(request)
            Log.d("AuthRepo", "Login exitoso: ${response.body()}")
            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            response.body()?.let { authResponse ->
                sessionManager.saveAuthData(authResponse.token, authResponse.user)
                authResponse
            } ?: throw NullPointerException("La respuesta del servidor está vacía")
        } catch (e: Exception) {
            // Registrar error para depuración
            Log.e("AuthRepository", "Error en login", e)
            throw e // Relanzar para ViewModel
        }
    }
}