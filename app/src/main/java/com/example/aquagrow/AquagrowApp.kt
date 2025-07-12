package com.example.aquagrow

import android.app.Application
import android.util.Log
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.example.aquagrow.ui.notifications.MqttNotificationHandler

class AquagrowApp : Application() {
    companion object {
        lateinit var instance: AquagrowApp
        var isInitialized = false
        var shouldNavigateToMain = false
        var alertListenerRegistered = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isInitialized = true

        // Verificar si hay sesión activa y no expirada
        shouldNavigateToMain = SessionManager.isLoggedIn() && !SessionManager.isTokenExpired()
        Log.d("AquagrowApp", "Sesión activa y válida: $shouldNavigateToMain")

        // Inicia conexión MQTT al abrir app
        MqttClientManager.connect()
        // Escuchar alertas en cualquier parte de la app
        if (!alertListenerRegistered) {
            MqttNotificationHandler.initGlobalAlertSubscription(applicationContext)
            alertListenerRegistered = true
        }
    }
}