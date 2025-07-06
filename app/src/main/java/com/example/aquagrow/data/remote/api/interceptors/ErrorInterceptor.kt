package com.example.aquagrow.data.remote.api.interceptors

import android.util.Log
import com.example.aquagrow.data.remote.responses.ApiError
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val errorBodyString = response.body?.string() ?: "No error body"
            Log.e("API_ERROR", "HTTP ${response.code}: $errorBodyString")
            throw ApiError(response, errorBodyString, "HTTP ${response.code}: $errorBodyString")
        }
        return response
    }
}