package com.example.aquagrow.data.remote.api.services

import com.example.aquagrow.data.model.requests.UserCreateRequest
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.model.requests.UserUpdRequest
import com.example.aquagrow.data.model.responses.TypesUserResponse
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.data.model.responses.UserCreUpdDelResponse
import com.example.aquagrow.data.model.responses.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @GET("user/get_user_types")
    suspend fun get_user_types () : Response<List<TypesUserResponse>>

    @GET("user/get_users_info")
    suspend fun get_users_info () : Response<List<UserInfoResponse>>

    @POST("user/get_user_info_by_id")
    suspend fun get_user_info_by_id (@Body request : UserDelLisRequest) : Response<UserInfoResponse>

    @GET("user/get_users_com_list")
    suspend fun get_users_com_list () : Response<List<UserComReponse>>

    @POST("user/get_user_com_by_id")
    suspend fun get_user_com_by_id (@Body request : UserDelLisRequest) : Response<UserComReponse>

    @POST("user/create_user")
    suspend fun create_user (@Body request: UserCreateRequest) : Response<UserCreUpdDelResponse>

    @POST("user/update_user")
    suspend fun update_user (@Body request : UserUpdRequest) : Response<UserCreUpdDelResponse>

    @POST("user/delete_user")
    suspend fun delete_user (@Body request: UserDelLisRequest) : Response<UserCreUpdDelResponse>
}