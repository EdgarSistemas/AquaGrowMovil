package com.example.aquagrow.controller

import android.content.Context
import com.example.aquagrow.core.network.ApiClient
import com.example.aquagrow.core.network.TokenManager
import com.example.aquagrow.core.network.services.AuthService
import com.example.aquagrow.models.requests.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthController(private val context : Context) {

    private val authService : AuthService = ApiClient.createService(AuthService::class.java)

    fun login(usuario : String, contrasenia : String, callback : (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authService.login(LoginRequest(usuario, contrasenia))
                if (response.success) {
                    TokenManager.saveToken(response.token)
                    callback(true, null)
                } else {
                    callback(false, response.message ?: "Error de autenticación")
                }
            } catch (e : Exception) {
                callback(false, "Error de conexión: ${e.message}")
            }
        }
    }

    fun isLoggedIn() : Boolean {
        return TokenManager.getToken() != null
    }

    fun logout() {
        TokenManager.clearToken()
    }
}