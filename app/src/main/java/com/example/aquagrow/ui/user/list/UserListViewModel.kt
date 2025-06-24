package com.example.aquagrow.ui.user.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.requests.UserDelLisRequest
import com.example.aquagrow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserListViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state

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
                users.forEachIndexed { i, user ->
                    Log.d("UserListVM", "Usuario $i: ${user.userInfo?.primer_nombre}, Estatus: ${user.userInfo?.estatus}")
                }

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
                val response = repository.delete_user(UserDelLisRequest(userId))
                if (response.id_usuario != null) {
                    _state.value = UserListState.DeleteSuccess(userId)
                } else {
                    _state.value = UserListState.Error("Error al eliminar usuario")
                }
            } catch (e: Exception) {
                _state.value = UserListState.Error("Error: ${e.message}")
            }
        }
    }
}