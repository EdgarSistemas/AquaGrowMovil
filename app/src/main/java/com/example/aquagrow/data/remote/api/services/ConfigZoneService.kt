package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.ConfigZoneAddRequest
import com.example.aquagrow.data.model.requests.ConfigZoneIddRequest
import com.example.aquagrow.data.model.requests.ConfigZoneUpdRequest
import com.example.aquagrow.data.model.requests.IrrigationAddRequest
import com.example.aquagrow.data.model.requests.IrrigationIdRequest
import com.example.aquagrow.data.model.requests.IrrigationUpdRequest
import com.example.aquagrow.data.model.requests.UnitIdRequest
import com.example.aquagrow.data.model.responses.ConfigZoneAddUpdResponse
import com.example.aquagrow.data.model.responses.ConfigZoneResponse
import com.example.aquagrow.data.model.responses.IrrigationAddResponse
import com.example.aquagrow.data.model.responses.IrrigationByIdResponse
import com.example.aquagrow.data.model.responses.UnitResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ConfigZoneService {
    @POST("configzone/get_config_zone_by_id")
    suspend fun getConfigZone(@Body request: ConfigZoneIddRequest): ConfigZoneResponse

    @POST("configzone/create_config_zone")
    suspend fun createConfigZone(@Body request: ConfigZoneAddRequest): ConfigZoneAddUpdResponse

    @POST("configzone/update_config_zone")
    suspend fun updateConfigZone(@Body request: ConfigZoneUpdRequest): ConfigZoneAddUpdResponse

    @POST("irrigationScheduling/get_irrigation_schedule_by_id")
    suspend fun getIrrigationSchedule(@Body request: IrrigationIdRequest): IrrigationByIdResponse

    @POST("irrigationScheduling/create_irrigation_schedule")
    suspend fun createIrrigationSchedule(@Body request: IrrigationAddRequest): IrrigationAddResponse

    @POST("irrigationScheduling/update_irrigation_schedule")
    suspend fun updateIrrigationSchedule(@Body request: IrrigationUpdRequest): IrrigationAddResponse

    @POST("unit/get_unit_complete_info")
    suspend fun get_unit_complete_info (@Body request: UnitIdRequest) : UnitResponse
}