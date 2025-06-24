package com.example.aquagrow.data.model.responses

import com.example.aquagrow.data.model.domain.Permission
import com.example.aquagrow.data.model.domain.UserType

// respuesta para consumir el endpoint:
// http://127.0.0.1:5000/api/user/get_user_types
data class TypesUserResponse (
    val id_tipo : Int,
    val nombre : String
)

// respuesta para consumir los endpoint:
// http://127.0.0.1:5000/api/user/get_users_info
// http://127.0.0.1:5000/api/user/get_user_info_by_id
data class UserInfoResponse(
    val id_usuario : Int?,
    val primer_nombre : String?,
    val apellido_pat : String?,
    val usuario : String?,
    val fecha_ultima_conexion : String?,
    val estatus : Int?
)

// respuesta para consumir los endpoint:
// http://127.0.0.1:5000/api/user/get_users_com_list
// http://127.0.0.1:5000/api/user/get_user_com_by_id
data class UserComReponse (
    val userInfo : UserInfoResponse?,
    val permisos : List<Permission?>,
    val tipo_usuario : UserType?,
    val mensaje : String?
)

// respuesta para consumir el endpoint
// http://127.0.0.1:5000/api/user/create_user
// http://127.0.0.1:5000/api/user/update_user
// http://127.0.0.1:5000/api/user/delete_user
data class UserCreUpdDelResponse(
    val id_usuario: Int?,
    val mensaje : String?
)