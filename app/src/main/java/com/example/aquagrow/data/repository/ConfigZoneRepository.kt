package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.model.responses.*
import com.example.aquagrow.data.remote.api.ApiClient
import com.example.aquagrow.data.remote.api.services.ConfigZoneService

class ConfigZoneRepository(
    private val api: ConfigZoneService = ApiClient.configZoneService
) {

    // Obtener configuración de zona
    suspend fun getConfigZone(idZona: Int): ConfigZoneResponse {
        Log.d("ConfigZoneRepo", "Obteniendo configuración de zona para ID $idZona")
        return try {
            val response = api.getConfigZone(ConfigZoneIddRequest(zona_id = idZona))
            Log.d("ConfigZoneRepo", "Config recibida: $response")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al obtener configuración de zona", e)
            throw e
        }
    }

    // Crear configuración de zona
    suspend fun createConfigZone(config: ConfigZoneAddRequest): ConfigZoneAddUpdResponse {
        Log.d("ConfigZoneRepo", "Creando configuración de zona: zonaId=${config.zone_id}")
        return try {
            val request = config
            val response = api.createConfigZone(request)
            Log.d("ConfigZoneRepo", "Configuración creada con ID: ${response.id_config}")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al crear configuración de zona", e)
            throw e
        }
    }

    // Actualizar configuración de zona
    suspend fun updateConfigZone(config: ConfigZoneUpdRequest): ConfigZoneAddUpdResponse {
        Log.d("ConfigZoneRepo", "Actualizando configuración de zona ID: ${config.id_config}")
        return try {
            val request = config
            val response = api.updateConfigZone(request)
            Log.d("ConfigZoneRepo", "Configuración actualizada")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al actualizar configuración de zona", e)
            throw e
        }
    }

    // Obtener programación de riego
    suspend fun getIrrigationSchedule(idProgramacion: Int): IrrigationByIdResponse {
        Log.d("ConfigZoneRepo", "Obteniendo programación riego ID: $idProgramacion")
        return try {
            val response = api.getIrrigationSchedule(IrrigationIdRequest(id_programacion = idProgramacion))
            Log.d("ConfigZoneRepo", "Programación recibida: $response")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al obtener programación de riego", e)
            throw e
        }
    }

    // Crear programación de riego
    suspend fun createIrrigationSchedule(request: IrrigationAddRequest): IrrigationAddResponse {
        Log.d("ConfigZoneRepo", "Creando programación de riego para zona ${request.zona_id}")
        return try {
            val response = api.createIrrigationSchedule(request)
            Log.d("ConfigZoneRepo", "Programación creada con ID: ${response.id_programacion_riego}")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al crear programación de riego", e)
            throw e
        }
    }

    // Actualizar programación de riego
    suspend fun updateIrrigationSchedule(request: IrrigationUpdRequest): IrrigationAddResponse {
        Log.d("ConfigZoneRepo", "Actualizando programación ID: ${request.id_programacion}")
        return try {
            val response = api.updateIrrigationSchedule(request)
            Log.d("ConfigZoneRepo", "Programación actualizada")
            response
        } catch (e: Exception) {
            Log.e("ConfigZoneRepo", "Error al actualizar programación de riego", e)
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
