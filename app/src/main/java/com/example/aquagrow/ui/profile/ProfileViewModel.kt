package com.example.aquagrow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.model.requests.UserUpdRequest
import com.example.aquagrow.data.model.responses.UserInfoResponse
import com.example.aquagrow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    private var user: UserInfoResponse? = null

    fun loadUserData(userId: Int) {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            try {
                user = repository.get_user_info_by_id(UserDelLisRequest(userId))
                user?.let {
                    _state.value = ProfileState.Loaded(it)
                }
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Error al cargar datos del perfil")
            }
        }
    }

    fun updateUser(nombre: String, apellido: String, usuario: String, password: String?) {
        val current = user ?: return
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            try {
                repository.update_user(
                    UserUpdRequest(
                        id_usuario = current.id_usuario!!,
                        primer_nombre = nombre,
                        apellido_pat = apellido,
                        usuario = usuario,
                        contrasenia = password
                    )
                )
                _state.value = ProfileState.Success
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Error al actualizar datos")
            }
        }
    }
}
