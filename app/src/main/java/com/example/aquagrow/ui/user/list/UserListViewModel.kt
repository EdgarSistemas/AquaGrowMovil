package com.example.aquagrow.ui.user.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserListViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state

    // Evento para refrescar la lista (usando SharedFlow para eventos únicos)
    private val _refreshEvent = MutableSharedFlow<Unit>()
    val refreshEvent = _refreshEvent.asSharedFlow()

    init {
        Log.d("UserListVM", "Inicializando ViewModel")
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                Log.d("UserListVM", "Cargando usuarios")
                _state.value = UserListState.Loading
                val users = repository.get_users_com_list()
                Log.d("UserListVM", "Usuarios recibidos: ${users.size}")

                _state.value = if (users.isEmpty()) {
                    UserListState.Empty
                } else {
                    UserListState.Success(users)
                }
            } catch (e: Exception) {
                _state.value = UserListState.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                _state.value = UserListState.Loading
                val request = UserDelLisRequest(userId)
                val response = repository.delete_user(request)

                if (response.id_usuario != null) {
                    // Disparar evento de actualización
                    triggerRefresh()
                    _state.value = UserListState.DeleteSuccess(userId)
                } else {
                    _state.value = UserListState.Error("No se pudo eliminar el usuario")
                }
            } catch (e: Exception) {
                _state.value = UserListState.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    // Función para disparar el evento de actualización
    fun triggerRefresh() {
        viewModelScope.launch {
            _refreshEvent.emit(Unit)
        }
    }
}