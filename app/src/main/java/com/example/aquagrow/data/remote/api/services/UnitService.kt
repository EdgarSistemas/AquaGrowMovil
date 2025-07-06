package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.UnitByUserRequest
import com.example.aquagrow.data.model.responses.UnitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UnitService {
    @POST("unit/get_units_with_user_and_device_by_user_id")
    suspend fun get_units_by_user_id(@Body request: UnitByUserRequest): Response<UnitResponse>
}