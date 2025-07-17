package com.example.aquagrow.data.model.requests

data class GrowingIdUnitRequest (
    val id_unidad : Int?,
    val id_cultivo : Int?
)

data class GrowingAddRequest (
    val unidad_id : Int,
    val nombre_planta : String,
    val dias_esperados : Int,
    val rendimiento_esperado : Int
)

data class GrowingUpdRequest (
    val id_unidad: Int,
    val id_cultivo : Int,
    val nombre_planta : String,
    val dias_esperados : Int,
    val rendimiento_esperado : Int
)

data class GrowingFinishRequest (
    val id_unidad : Int,
    val id_cultivo : Int,
    val motivo_terminacion : String,
    val dias_real : Int,
    val rendimiento_real : Int,
    val estatus_final : Int
)