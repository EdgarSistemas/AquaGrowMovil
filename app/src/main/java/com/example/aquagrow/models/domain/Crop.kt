package com.example.aquagrow.models.domain

import java.util.Date

data class Crop(
    val id_cultivo: Int,
    val unidad_id: Int,
    val nombre_planta: String,
    val fecha_inicio: Date,
    val fecha_fin: Date?,
    val dias_esperados: Int?,
    val dias_real: Int?,
    val rendimiento_esperado: Double?,
    val rendimiento_real: Double?,
    val estatus: Int,
    val estatus_final: String?,
    val motivo_terminacion: String?
)