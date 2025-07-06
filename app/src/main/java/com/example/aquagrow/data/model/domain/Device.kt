package com.example.aquagrow.data.model.domain

data class Device (
    val id_dispositivo : Int,
    val nombre : String,
    val tipo : String,
    val mac_address : String?,
    val topico_comando : String?,
    val topico_Respuesta : String?,
    val estatus : Int
)