package com.example.aquagrow.data.model.responses

import com.example.aquagrow.data.model.domain.Unit

data class UnitResponse(
    val units: List<Unit>
)

data class UnitsResponse(
    val units: Unit
)

data class UnitUpdateValuesResponse (
    val id_unidad : Int,
    val mensaje : String
)

data class UnitRemoveUserResponse (
    val id_usuario : Int,
    val mensaje: String
)

data class ConfigZoneResponse (
    val id_config : Int,
    val zona_id : Int,
    val temp_min : Double,
    val temp_max : Double
)

data class ConfigZoneAddUpdResponse (
    val id_config : Int,
    val mensaje : String
)

data class IrrigationByIdResponse (
    val id_programacion : Int,
    val zona_id : Int,
    val hora_inicio : String,
    val duracion_minutos : Int,
    val tipo_frecuencia : String,
    val dias_semana : String,
    val frecuencia_dia : Int,
    val intervalo_minutos : Int,
    val estatus : Int
)

data class IrrigationAddResponse (
    val id_programacion_riego : Int,
    val mensaje : String
)

data class ConfigTankResponse(
    val id_config: Int,
    val estanque_id: Int,
    val temp_agua_min: Double,
    val temp_agua_max: Double,
    val ph_min: Double,
    val ph_max: Double,
    val dist_min: Double,
    val dist_max: Double
)

data class ConfigTankAddUpdResponse(
    val id_config: Int,
    val mensaje: String
)

data class FeedingScheduleResponse(
    val id_programacion: Int,
    val estanque_id: Int,
    val hora_inicio: String,
    val duracion_minutos: Int,
    val tipo_frecuencia: String,
    val dias_semana: String?,
    val frecuencia_dia: Int?,
    val intervalo_minutos: Int?,
    val estatus: Int
)

data class FeedingScheduleAddUpdResponse(
    val id_programacion: Int,
    val mensaje: String
)