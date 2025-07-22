package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.model.responses.*
import com.example.aquagrow.data.remote.api.ApiClient
import com.example.aquagrow.data.remote.api.services.TankConfigService

class TankConfigRepository(
    private val api: TankConfigService = ApiClient.tankConfigService
) {

    // Obtener configuración de estanque
    suspend fun getConfigTank(estanqueId: Int): ConfigTankResponse {
        Log.d("TankConfigRepo", "Obteniendo configuración para estanque ID: $estanqueId")
        return try {
            val response = api.getConfigTank(ConfigTankIdRequest(estanque_id = estanqueId))
            Log.d("TankConfigRepo", "Configuración de estanque recibida: $response")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al obtener configuración de estanque", e)
            throw e
        }
    }

    // Crear configuración de estanque
    suspend fun createConfigTank(request: ConfigTankAddRequest): ConfigTankAddUpdResponse {
        Log.d("TankConfigRepo", "Creando configuración para estanque ID: ${request.estanque_id}")
        return try {
            val response = api.createConfigTank(request)
            Log.d("TankConfigRepo", "Configuración creada con ID: ${response.id_config}")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al crear configuración de estanque", e)
            throw e
        }
    }

    // Actualizar configuración de estanque
    suspend fun updateConfigTank(request: ConfigTankUpdRequest): ConfigTankAddUpdResponse {
        Log.d("TankConfigRepo", "Actualizando configuración ID: ${request.id_config}")
        return try {
            val response = api.updateConfigTank(request)
            Log.d("TankConfigRepo", "Configuración de estanque actualizada")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al actualizar configuración de estanque", e)
            throw e
        }
    }

    // Obtener programación de alimentación
    suspend fun getFeedingSchedule(id_programacion: Int): FeedingScheduleResponse {
        Log.d("TankConfigRepo", "Obteniendo programación de alimentación para estanque ID: $id_programacion")
        return try {
            val response = api.getFeedingSchedule(FeedingScheduleIdRequest(id_programacion = id_programacion))
            Log.d("TankConfigRepo", "Programación de alimentación recibida: $response")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al obtener programación de alimentación", e)
            throw e
        }
    }

    // Crear programación de alimentación
    suspend fun createFeedingSchedule(request: FeedingScheduleAddRequest): FeedingScheduleAddUpdResponse {
        Log.d("TankConfigRepo", "Creando programación de alimentación para estanque ID: ${request.estanque_id}")
        return try {
            val response = api.createFeedingSchedule(request)
            Log.d("TankConfigRepo", "Programación creada con ID: ${response.id_programacion}")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al crear programación de alimentación", e)
            throw e
        }
    }

    // Actualizar programación de alimentación
    suspend fun updateFeedingSchedule(request: FeedingScheduleUpdRequest): FeedingScheduleAddUpdResponse {
        Log.d("TankConfigRepo", "Actualizando programación de alimentación ID: ${request.id_programacion}")
        return try {
            val response = api.updateFeedingSchedule(request)
            Log.d("TankConfigRepo", "Programación de alimentación actualizada")
            response
        } catch (e: Exception) {
            Log.e("TankConfigRepo", "Error al actualizar programación de alimentación", e)
            throw e
        }
    }

    // recuperar la informacion completa de la unidad
    suspend fun getUnitByIdWithZoneTankUser(request : Int) : Unit {
        Log.d("ConfigZoneRepo", "Obteniendo unidad ID: $request")
        return try {
            val r = UnitIdRequest(request)
            val response = api.get_unit_complete_info(r)
            Log.d("ConfigZoneRepo", "Unidad recibida: $response")
            response.units.get(0)
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al obtener programación de riego", e)
            throw e
        }
    }
}