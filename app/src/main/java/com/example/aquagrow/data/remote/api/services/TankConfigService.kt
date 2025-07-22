package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.ConfigTankAddRequest
import com.example.aquagrow.data.model.requests.ConfigTankIdRequest
import com.example.aquagrow.data.model.requests.ConfigTankUpdRequest
import com.example.aquagrow.data.model.requests.FeedingScheduleAddRequest
import com.example.aquagrow.data.model.requests.FeedingScheduleIdRequest
import com.example.aquagrow.data.model.requests.FeedingScheduleUpdRequest
import com.example.aquagrow.data.model.requests.UnitIdRequest
import com.example.aquagrow.data.model.responses.ConfigTankAddUpdResponse
import com.example.aquagrow.data.model.responses.ConfigTankResponse
import com.example.aquagrow.data.model.responses.FeedingScheduleAddUpdResponse
import com.example.aquagrow.data.model.responses.FeedingScheduleResponse
import com.example.aquagrow.data.model.responses.UnitResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TankConfigService {
    @POST("configtank/get_config_tank_by_tank_id")
    suspend fun getConfigTank(@Body request: ConfigTankIdRequest): ConfigTankResponse

    @POST("configtank/create_config_tank")
    suspend fun createConfigTank(@Body request: ConfigTankAddRequest): ConfigTankAddUpdResponse

    @POST("configtank/update_config_tank")
    suspend fun updateConfigTank(@Body request: ConfigTankUpdRequest): ConfigTankAddUpdResponse

    @POST("feedingschedule/get_feeding_schedule_by_tank_id")
    suspend fun getFeedingSchedule(@Body request: FeedingScheduleIdRequest): FeedingScheduleResponse

    @POST("feedingschedule/create_feeding_schedule")
    suspend fun createFeedingSchedule(@Body request: FeedingScheduleAddRequest): FeedingScheduleAddUpdResponse

    @POST("feedingschedule/update_feeding_schedule")
    suspend fun updateFeedingSchedule(@Body request: FeedingScheduleUpdRequest): FeedingScheduleAddUpdResponse

    @POST("unit/get_unit_complete_info")
    suspend fun get_unit_complete_info (@Body request: UnitIdRequest) : UnitResponse
}