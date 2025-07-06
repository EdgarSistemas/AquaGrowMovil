package com.example.aquagrow.data.model.requests

// Para crear (id_usuario = 0) o modificar usuarios (id_usuario > 0)
data class UserCreateRequest (
    val primer_nombre : String,
    val apellido_pat : String,
    val usuario : String,
    val contrasenia : String,
    val tipo_id: Int
)

// Para modificar usuarios (id_usuario > 0)
data class UserUpdRequest (
    val id_usuario : Int,
    val primer_nombre : String,
    val apellido_pat : String,
    val usuario : String,
    val contrasenia : String?,
    // val contrasenia_new : String,
)

// para eliminar/listar un usuario especifico
data class UserDelLisRequest (
    val id_usuario: Int
)