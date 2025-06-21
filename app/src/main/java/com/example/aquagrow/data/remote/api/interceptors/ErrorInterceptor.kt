package com.example.aquagrow.data.remote.api.interceptors

import com.example.aquagrow.data.remote.responses.ApiError
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            throw ApiError(
                code = response.code,
                message = response.message ?: "Error desconocido"
            )
        }

        return response
    }
}