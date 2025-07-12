package com.example.aquagrow.data.model.domain

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    @SerializedName("id_usuario") val id_usuario: Int,
    @SerializedName("primer_nombre") val primer_nombre: String,
    @SerializedName("apellido_pat") val apellido_pat: String,
    @SerializedName("usuario") val usuario: String,
    @SerializedName("estatus") val estatus: Int,

    @SerializedName("fecha_ultima_conexion")
    val fecha_ultima_conexion: Date?,

    @SerializedName("tipo_usuario") val tipo_usuario: UserType?,
    @SerializedName("permisos") val permisos: List<Permission>?
)

data class UserType (
    val id_tipo: Int,
    val nombre: String
)

data class Permission(
    val id_permiso : Int,
    val nombre_modulo : String,
    val icono : String
)