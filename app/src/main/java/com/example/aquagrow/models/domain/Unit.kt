package com.example.aquagrow.models.domain

data class Unit(
    val id_unidad: Int,
    val nombre: String,
    val zona_id: Int,
    val estanque_id: Int,
    val usuario_id: Int,
    val estatus: Int
)