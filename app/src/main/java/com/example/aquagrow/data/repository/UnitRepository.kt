// En data/repository/UnitRepository.kt
package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.UniUserIdRequest
import com.example.aquagrow.data.model.requests.UnitByUserRequest
import com.example.aquagrow.data.model.requests.UnitIdRequest
import com.example.aquagrow.data.model.requests.UnitValuesUpdateRequest
import com.example.aquagrow.data.remote.api.ApiClient
import javax.inject.Inject

class UnitRepository @Inject constructor() {

    suspend fun getUnitsForCurrentUser(): List<Unit> {
        Log.d("UnitRepo", "Obteniendo la lista de unidades en base al id dle usuario que inicio sesion")
        return try {
            // Obtener ID del usuario actual desde SessionManager
            val userId = SessionManager.getUserId() ?: throw Exception("Usuario no autenticado")

            val request = UnitByUserRequest(id_usuario = userId)
            val response = ApiClient.unitService.get_units_by_user_id(request)

            if (!response.isSuccessful) {
                throw Exception("Error al obtener unidades: ${response.code()}")
            }

            val unitResponse = response.body() ?: throw Exception("Respuesta vacía del servidor")
            unitResponse.units
        } catch (e: Exception) {
            Log.e("UnitRepository", "Error: ${e.message}", e)
            throw e
        }
    }

    suspend fun getAllUnitsWithZoneTankUser(): List<Unit> {
        Log.d("UnitRepo", "Obteniendo todas las unidades con zona, estanque y usuario")
        return try {
            val response = ApiClient.unitService.get_units_with_zone_tank_user()

            if (!response.isSuccessful) {
                throw Exception("Error al obtener unidades: ${response.code()}")
            }

            val unitResponse = response.body() ?: throw Exception("Respuesta vacía del servidor")
            unitResponse.units
        } catch (e: Exception) {
            Log.e("UnitRepository", "Error: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateUnitValues(idUnidad: Int, nombreUnidad: String, nombreEstanque: String, nombreZona: String): String {
        Log.d("UnitRepo", "Actualizando valores de unidad $idUnidad")

        return try {
            val request = UnitValuesUpdateRequest(
                id_unidad = idUnidad,
                nombre_unidad = nombreUnidad,
                nombre_estanque = nombreEstanque,
                nombre_zona = nombreZona
            )
            val response = ApiClient.unitService.update_unit_values(request)

            if (!response.isSuccessful) {
                throw Exception("Error al actualizar valores: ${response.code()}")
            }

            val responseBody = response.body() ?: throw Exception("Respuesta vacía del servidor")
            responseBody.mensaje
        } catch (e: Exception) {
            Log.e("UnitRepository", "Error en updateUnitValues: ${e.message}", e)
            throw e
        }
    }

    suspend fun removeUserFromUnit(idUnidad: Int): String {
        Log.d("UnitRepo", "Removiendo usuario de unidad $idUnidad")

        return try {
            val request = UnitIdRequest(id_unidad = idUnidad)
            val response = ApiClient.unitService.remove_user(request)

            if (!response.isSuccessful) {
                throw Exception("Error al remover usuario: ${response.code()}")
            }

            val responseBody = response.body() ?: throw Exception("Respuesta vacía del servidor")
            responseBody.mensaje
        } catch (e: Exception) {
            Log.e("UnitRepository", "Error en removeUserFromUnit: ${e.message}", e)
            throw e
        }
    }

    suspend fun assignUserToUnit(idUnidad: Int, idUsuario: Int): String {
        Log.d("UnitRepo", "Asignando usuario $idUsuario a unidad $idUnidad")

        return try {
            val request = UniUserIdRequest(id_unidad = idUnidad, id_usuario = idUsuario)
            val response = ApiClient.unitService.assign_user(request)

            if (!response.isSuccessful) {
                throw Exception("Error al asignar usuario: ${response.code()}")
            }

            val responseBody = response.body() ?: throw Exception("Respuesta vacía del servidor")
            responseBody.mensaje
        } catch (e: Exception) {
            Log.e("UnitRepository", "Error en assignUserToUnit: ${e.message}", e)
            throw e
        }
    }
}