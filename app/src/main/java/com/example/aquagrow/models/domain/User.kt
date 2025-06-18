package com.example.aquagrow.models.domain

data class User(
    val id_usuario: Int,
    val primer_nombre: String?,
    val apellido_pat: String?,
    val usuario: String,
    val contrasenia: String? = null,
    val fecha_ultima_conexion: String, // Formato ISO 8601: "2023-12-31T23:59:59"
    val estatus: Int,
    val tipo_id: Int
)