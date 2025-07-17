package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.UniUserIdRequest
import com.example.aquagrow.data.model.requests.UnitByUserRequest
import com.example.aquagrow.data.model.requests.UnitIdRequest
import com.example.aquagrow.data.model.requests.UnitValuesUpdateRequest
import com.example.aquagrow.data.model.responses.UnitRemoveUserResponse
import com.example.aquagrow.data.model.responses.UnitResponse
import com.example.aquagrow.data.model.responses.UnitUpdateValuesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UnitService {
    @POST("unit/get_units_with_user_and_device_by_user_id")
    suspend fun get_units_by_user_id(@Body request: UnitByUserRequest): Response<UnitResponse>

    @GET("unit/get_units_with_zone_tank_user")
    suspend fun get_units_with_zone_tank_user() : Response<UnitResponse>

    @POST("unit/update_unit_values")
    suspend fun update_unit_values (@Body request: UnitValuesUpdateRequest) : Response<UnitUpdateValuesResponse>

    @POST("unit/remove_user")
    suspend fun remove_user (@Body request: UnitIdRequest) : Response<UnitRemoveUserResponse>

    @POST("unit/assign_user")
    suspend fun assign_user (@Body request: UniUserIdRequest) : Response<UnitRemoveUserResponse>

    @POST("unit/get_units_with_growing_zone_tank_user")
    suspend fun get_units_with_growing_zone_tank_user (@Body request: UniUserIdRequest) : Response<UnitResponse>
}