package com.example.aquagrow.data.repository

import android.util.Log
import com.example.aquagrow.data.model.domain.Growing
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.model.responses.*
import com.example.aquagrow.data.remote.api.ApiClient
import com.example.aquagrow.data.remote.api.services.GrowingService
import retrofit2.HttpException

class GrowingRepository(
    private val growingService: GrowingService = ApiClient.growingService
) {

    // 1. Obtener cultivo activo por unidad
    suspend fun get_growing_by_unit_id(idUnidad: Int): Growing {
        Log.d("GrowingRepo", "Obteniendo cultivo por unidad ID: $idUnidad")
        return try {
            val request = GrowingIdUnitRequest(id_unidad = idUnidad, id_cultivo = null)
            val response = growingService.get_growings_by_unit_id(request)
            if (!response.isSuccessful) throw HttpException(response)

            val growing = response.body()?.growings?.firstOrNull()
                ?: throw Exception("No hay cultivo activo en la unidad")
            Log.d("GrowingRepo", "Cultivo encontrado: ${growing.nombre_planta}")
            growing
        } catch (e: Exception) {
            Log.e("GrowingRepo", "Error al obtener cultivo activo", e)
            throw e
        }
    }

    // 2. Obtener cultivo por ID
    suspend fun get_growing_by_id(idUnidad: Int, idCultivo: Int): Growing {
        Log.d("GrowingRepo", "Obteniendo cultivo ID: $idCultivo (unidad: $idUnidad)")
        return try {
            val request = GrowingIdUnitRequest(id_unidad = idUnidad, id_cultivo = idCultivo)
            val response = growingService.get_growing_by_id(request)
            if (!response.isSuccessful) throw HttpException(response)

            val growing = response.body()?.growing
                ?: throw Exception("Cultivo no encontrado")
            Log.d("GrowingRepo", "Cultivo encontrado: ${growing.nombre_planta}")
            growing
        } catch (e: Exception) {
            Log.e("GrowingRepo", "Error al obtener cultivo por ID", e)
            throw e
        }
    }

    // 3. Crear cultivo
    suspend fun create_growing(request: GrowingAddRequest): GrowingAddUpdResponse {
        Log.d("GrowingRepo", "Creando cultivo: ${request.nombre_planta}")
        return try {
            val response = growingService.create_growing(request)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "Error desconocido")
            }

            response.body()?.let {
                Log.d("GrowingRepo", "Cultivo creado ID: ${it.id_cultivo}")
                it
            } ?: throw Exception("Respuesta vacía del servidor")
        } catch (e: Exception) {
            Log.e("GrowingRepo", "Error al crear cultivo", e)
            throw e
        }
    }

    // 4. Actualizar cultivo
    suspend fun update_growing(request: GrowingUpdRequest): GrowingAddUpdResponse {
        Log.d("GrowingRepo", "Actualizando cultivo ID: ${request.id_cultivo}")
        return try {
            val response = growingService.update_growing(request)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "Error desconocido")
            }

            response.body()?.let {
                Log.d("GrowingRepo", "Cultivo actualizado ID: ${it.id_cultivo}")
                it
            } ?: throw Exception("Respuesta vacía del servidor")
        } catch (e: Exception) {
            Log.e("GrowingRepo", "Error al actualizar cultivo", e)
            throw e
        }
    }

    // 5. Terminar cultivo
    suspend fun terminate_growing(request: GrowingFinishRequest): GrowingAddUpdResponse {
        Log.d("GrowingRepo", "Terminando cultivo ID: ${request.id_cultivo}")
        return try {
            val response = growingService.terminate_growing(request)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "Error desconocido")
            }

            response.body()?.let {
                Log.d("GrowingRepo", "Cultivo terminado correctamente")
                it
            } ?: throw Exception("Respuesta vacía del servidor")
        } catch (e: Exception) {
            Log.e("GrowingRepo", "Error al terminar cultivo", e)
            throw e
        }
    }
}
