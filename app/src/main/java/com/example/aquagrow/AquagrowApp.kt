package com.example.aquagrow

import android.app.Application
import android.util.Log
import com.example.aquagrow.data.local.SessionManager

class AquagrowApp : Application() {
    companion object {
        lateinit var instance: AquagrowApp
        var isInitialized = false
        var shouldNavigateToMain = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isInitialized = true

        // Verificar si hay sesión activa y no expirada
        shouldNavigateToMain = SessionManager.isLoggedIn() && !SessionManager.isTokenExpired()

        Log.d("AquagrowApp", "Sesión activa y válida: $shouldNavigateToMain")
    }
}