package com.example.aquagrow.core.network.interceptors

import com.example.aquagrow.core.network.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain : Interceptor.Chain) : Response {
        val originalRequest = chain.request()
        // no agregar token a endpoint de autenticacion
        if (originalRequest.url.encodedPath.contains("auth/login")) {
            return chain.proceed(originalRequest)
        }

        val token = TokenManager.getToken() ?: return chain.proceed(originalRequest)

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}