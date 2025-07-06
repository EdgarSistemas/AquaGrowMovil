package com.example.aquagrow.ui.user.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.UserType
import com.example.aquagrow.data.model.requests.UserCreateRequest
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.model.requests.UserUpdRequest
import com.example.aquagrow.data.model.responses.TypesUserResponse
import com.example.aquagrow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserFormViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UserFormState>(UserFormState.Loading)
    val state: StateFlow<UserFormState> = _state

    fun loadUserTypes() {
        viewModelScope.launch {
            try {
                _state.value = UserFormState.Loading
                val types = repository.get_user_types().map {
                    UserType(it.id_tipo, it.nombre)
                }
                _state.value = UserFormState.TypesLoaded(types)
            } catch (e: Exception) {
                _state.value = UserFormState.Error("Error loading types: ${e.message}")
            }
        }
    }

    fun loadUserData(userId: Int) {
        viewModelScope.launch {
            try {
                _state.value = UserFormState.Loading
                val request = UserDelLisRequest(userId)
                val user = repository.get_user_com_by_id(request)
                _state.value = UserFormState.UserLoaded(user)
            } catch (e: Exception) {
                _state.value = UserFormState.Error("Error loading user: ${e.message}")
            }
        }
    }

    fun createUser(firstName: String, lastName: String, username: String, password: String, userTypeId: Int) {
        viewModelScope.launch {
            try {
                _state.value = UserFormState.Loading
                val request = UserCreateRequest(
                    primer_nombre = firstName,
                    apellido_pat = lastName,
                    usuario = username,
                    contrasenia = password,
                    tipo_id = userTypeId
                )
                repository.create_user(request)
                _state.value = UserFormState.CreateSuccess
            } catch (e: Exception) {
                // Usar el mensaje directo de la excepción
                _state.value = UserFormState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateUser(userId: Int, firstName: String, lastName: String, username: String, password: String?) {
        viewModelScope.launch {
            try {
                _state.value = UserFormState.Loading
                val request = UserUpdRequest(
                    id_usuario = userId,
                    primer_nombre = firstName,
                    apellido_pat = lastName,
                    usuario = username,
                    contrasenia = password
                )
                repository.update_user(request)
                _state.value = UserFormState.UpdateSuccess
            } catch (e: Exception) {
                // Usar el mensaje directo de la excepción
                _state.value = UserFormState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}