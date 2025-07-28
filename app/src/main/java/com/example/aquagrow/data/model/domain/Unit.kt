package com.example.aquagrow.data.model.domain

data class Unit(
    val id_unidad : Int,
    val nombre : String,
    val estatus : Int,
    val zone : Zone?,
    val tank : Tank?,
    val user: User?,
    val growing: Growing?,
    val dispositivo: Device?,
    val sensore : Telemetry?
)

data class Zone (
    val id_zona : Int,
    val nombre : String,
    val estatus : Int,
    val configZone: ConfigZone?,
    val programacionRiego: ProgramacionRiego?
)

data class ConfigZone (
    val id_config : Int,
    val temp_min : Double,
    val temp_max : Double
)

data class ProgramacionRiego (
    val id_programacion : Int,
    val hora_inicio : String,
    val duracion_minutos : Int,
    val tipo_frecuencia : String,
    val dias_semana : String,
    val frecuencia_dia : Int,
    val intervalo_minutos : Int,
    val estatus : Int
)

data class  Tank (
    val id_estanque : Int,
    val nombre : String,
    val estatus : Int,
    val configTank: ConfigTank?,
    val programacionAlimentacion: ProgramacionAlimentacion?
)

data class  ConfigTank (
    val id_config : Int,
    val temp_agua_min : Double,
    val temp_agua_max : Double,
    val ph_min : Double,
    val ph_max : Double,
    val dist_min : Double,
    val dist_max: Double
)

data class ProgramacionAlimentacion (
    val id_programacion: Int,
    val hora_inicio: String,
    val duracion_minutos: Int,
    val tipo_frecuencia: String,
    val dias_semana: String,
    val frecuencia_dia: Int,
    val intervalo_minutos: Int,
    val estatus: Int
)

data class Telemetry(
    val tempZona: Double,
    val tempAgua: Double,
    val ph: Double,
    val humedad: Double?,
    val nivel: Double?
)