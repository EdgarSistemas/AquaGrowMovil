package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.GrowingAddRequest
import com.example.aquagrow.data.model.requests.GrowingFinishRequest
import com.example.aquagrow.data.model.requests.GrowingIdUnitRequest
import com.example.aquagrow.data.model.requests.GrowingUpdRequest
import com.example.aquagrow.data.model.responses.GrowingAddUpdResponse
import com.example.aquagrow.data.model.responses.GrowingIdUnitResponse
import com.example.aquagrow.data.model.responses.GrowingsIdUnitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GrowingService {
    @POST("growing/get_growings_by_unit_id")
    suspend fun get_growings_by_unit_id (@Body request : GrowingIdUnitRequest) : Response<GrowingsIdUnitResponse>

    @POST("growing/get_growing_by_id")
    suspend fun get_growing_by_id (@Body request : GrowingIdUnitRequest) : Response<GrowingIdUnitResponse>

    @POST("growing/create_growing")
    suspend fun create_growing (@Body request : GrowingAddRequest) : Response<GrowingAddUpdResponse>

    @POST("growing/update_growing")
    suspend fun update_growing (@Body request: GrowingUpdRequest) : Response<GrowingAddUpdResponse>

    @POST("growing/terminate_growing")
    suspend fun terminate_growing (@Body request: GrowingFinishRequest) : Response<GrowingAddUpdResponse>
}