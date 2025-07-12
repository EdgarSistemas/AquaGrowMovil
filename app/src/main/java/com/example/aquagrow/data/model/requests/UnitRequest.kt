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