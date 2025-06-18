package com.example.aquagrow.core.network

import java.util.concurrent.TimeUnit
import com.example.aquagrow.core.network.interceptors.AuthInterceptor
import com.example.aquagrow.core.network.interceptors.ErrorInterceptor
import com.example.aquagrow.core.network.interceptors.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://localhost:7091/api"

    // instancia de OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(AuthInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // instancia de retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService (serviceClass : Class<T>) : T {
        return retrofit.create(serviceClass)
    }
}