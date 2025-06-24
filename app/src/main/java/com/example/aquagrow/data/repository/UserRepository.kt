package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.model.requests.UserCreateRequest
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.model.requests.UserUpdRequest
import com.example.aquagrow.data.model.responses.TypesUserResponse
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.data.model.responses.UserCreUpdDelResponse
import com.example.aquagrow.data.model.responses.UserInfoResponse
import com.example.aquagrow.data.remote.api.ApiClient
import com.example.aquagrow.data.remote.api.services.UserService
import retrofit2.HttpException

class UserRepository (
    private val userService : UserService = ApiClient.userService
) {
    // 1. Obtener tipos de usuario
    suspend fun get_user_types(): List<TypesUserResponse> {
        Log.d("UserRepo", "Obteniendo tipos de usuario")
        return try {
            val response = userService.get_user_types()
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Tipos de usuario obtenidos: ${it.size} registros")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al obtener tipos de usuario", e)
            throw e
        }
    }

    // 2. Obtener información básica de todos los usuarios
    suspend fun get_users_info(): List<UserInfoResponse> {
        Log.d("UserRepo", "Obteniendo información básica de usuarios")
        return try {
            val response = userService.get_users_info()
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuarios obtenidos: ${it.size} registros")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al obtener información de usuarios", e)
            throw e
        }
    }

    // 3. Obtener información de un usuario por ID
    suspend fun get_user_info_by_id(request: UserDelLisRequest): UserInfoResponse {
        Log.d("UserRepo", "Buscando usuario por ID: ${request.id_usuario}")
        return try {
            val response = userService.get_user_info_by_id(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuario encontrado: ${it.primer_nombre}")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al buscar usuario por ID", e)
            throw e
        }
    }

    // 4. Obtener lista completa de usuarios (con permisos y tipo)
    suspend fun get_users_com_list(): List<UserComReponse> {
        Log.d("UserRepo", "Obteniendo lista completa de usuarios")
        return try {
            val response = userService.get_users_com_list()
            response.body()?.forEach {
                Log.d("UserRepo", "Obteniendo lista completa de usuarios ${it.toString()}")
                println(it)
            }
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Lista completa obtenida: ${it.size} registros")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al obtener lista completa", e)
            throw e
        }
    }

    // 5. Obtener usuario completo por ID
    suspend fun get_user_com_by_id(request: UserDelLisRequest): UserComReponse {
        Log.d("UserRepo", "Buscando usuario completo por ID: ${request.id_usuario}")
        return try {
            val response = userService.get_user_com_by_id(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuario completo encontrado")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al buscar usuario completo", e)
            throw e
        }
    }

    // 6. Crear nuevo usuario
    suspend fun create_user(request: UserCreateRequest): UserCreUpdDelResponse {
        Log.d("UserRepo", "Creando nuevo usuario: ${request.usuario}")
        return try {
            val response = userService.create_user(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuario creado ID: ${it.id_usuario}")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al crear usuario", e)
            throw e
        }
    }

    // 7. Actualizar usuario existente
    suspend fun update_user(request: UserUpdRequest): UserCreUpdDelResponse {
        Log.d("UserRepo", "Actualizando usuario ID: ${request.id_usuario}")
        return try {
            val response = userService.update_user(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuario actualizado: ${it.mensaje}")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al actualizar usuario", e)
            throw e
        }
    }

    // 8. Eliminar usuario
    suspend fun delete_user(request: UserDelLisRequest): UserCreUpdDelResponse {
        Log.d("UserRepo", "Eliminando usuario ID: ${request.id_usuario}")
        return try {
            val response = userService.delete_user(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()!!.also {
                Log.d("UserRepo", "Usuario eliminado: ${it.mensaje}")
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error al eliminar usuario", e)
            throw e
        }
    }


}