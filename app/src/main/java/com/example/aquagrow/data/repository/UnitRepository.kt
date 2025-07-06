// En data/repository/UnitRepository.kt
package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.UnitByUserRequest
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
}