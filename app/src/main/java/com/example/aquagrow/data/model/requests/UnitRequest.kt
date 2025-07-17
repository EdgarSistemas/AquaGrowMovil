package com.example.aquagrow.data.model.requests

data class UnitByUserRequest(
    val id_usuario: Int
)

data class UnitIdRequest (
    val id_unidad : Int
)

data class UniUserIdRequest (
    val id_unidad: Int,
    val id_usuario: Int
)

data class UnitValuesUpdateRequest (
    val id_unidad : Int,
    val nombre_unidad : String,
    val nombre_estanque : String,
    val nombre_zona : String
)

data class ConfigZoneIddRequest (
    val zona_id : Int
)

data class ConfigZoneAddRequest (
    val zone_id : Int,
    val temp_min : Double,
    val temp_max : Double
)

data class ConfigZoneUpdRequest (
    val id_config : Int,
    val temp_min : Double,
    val temp_max : Double
)

data class IrrigationIdRequest (
    val id_programacion : Int
)

data class IrrigationAddRequest (
    val zona_id : Int,
    val hora_inicio : String,
    val duracion_minutos : Int,
    val tipo_frecuencia : String,
    val dias_semana : String,
    val frecuencia_dia : Int,
    val intervalo_minutos : Int
)

data class IrrigationUpdRequest (
    val id_programacion : Int,
    val hora_inicio : String,
    val duracion_minutos : Int,
    val tipo_frecuencia : String,
    val dias_semana : String,
    val frecuencia_dia : Int,
    val intervalo_minutos : Int
)