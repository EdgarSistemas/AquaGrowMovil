package com.example.aquagrow.data.remote.api

import java.util.concurrent.TimeUnit
import com.example.aquagrow.data.remote.api.interceptors.AuthInterceptor
import com.example.aquagrow.data.remote.api.interceptors.ErrorInterceptor
import com.example.aquagrow.data.remote.api.services.AuthService
import com.example.aquagrow.data.remote.api.services.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.100.159:5000/api/"

    // Singleton perezoso seguro para el cliente HTTP
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor())
            .addInterceptor(ErrorInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Formato ISO 8601
            .create()
    }

    // Singleton perezoso seguro para Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // =====================================
    // seccion para declarar servicios
    // =====================================
    // Singleton perezoso seguro para el servicio de autenticación
    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // Singleton perezoso seguro para el servicio de usuarios
    val userService : UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}