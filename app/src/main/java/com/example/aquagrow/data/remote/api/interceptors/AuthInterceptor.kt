package com.example.aquagrow.data.remote.api.interceptors

import android.content.Intent
import android.util.Log
import com.example.aquagrow.AquagrowApp
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.ui.auth.LoginActivity
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlin.jvm.Throws

class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Excluir endpoints de autenticación de cualquier verificación
        if (request.url.encodedPath.contains("auth/login")) {
            return chain.proceed(request)
        }

        // Obtener token
        val token = SessionManager.getToken()

        // Si no hay token, proceder normalmente sin autenticación
        if (token == null) {
            return chain.proceed(request)
        }

        // Verificar solo si hay token y está expirado
        if (SessionManager.isTokenExpired()) {
            Log.w("AuthInterceptor", "Token expirado, cerrando sesión")
            SessionManager.clearAuthData()

            val intent = Intent(AquagrowApp.instance, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("SESSION_EXPIRED", true)
            }
            AquagrowApp.instance.startActivity(intent)

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Session expired")
                .body("Session expired".toResponseBody())
                .build()
        }

        // 5. Si el token es válido, añadirlo a la petición
        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }
}