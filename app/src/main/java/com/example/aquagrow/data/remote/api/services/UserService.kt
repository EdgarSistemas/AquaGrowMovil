package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.responses.UserReponse
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("auth/get_users")
    suspend fun get_users () : Response<UserReponse>
}