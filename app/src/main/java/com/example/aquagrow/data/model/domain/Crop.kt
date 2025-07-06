package com.example.aquagrow.data.model.domain

import java.util.Date

data class Growing(
    val id_cultivo: Int,
    val nombre_planta: String,
    val fecha_inicio: String,
    val fecha_fin: String?,
    val dias_esperados: Int,
    val dias_real: Int?,
    val rendimiento_esperado: Int,
    val rendimiento_real: Int?,
    val estatus: Int,
    val estatus_final: String?,
    val motivo_terminacion: String?
)