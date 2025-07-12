package com.example.aquagrow.ui.assignUnit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.data.model.responses.UserInfoResponse
import com.example.aquagrow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSearchViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _allUsers = MutableStateFlow<List<UserInfoResponse>>(emptyList())
    private val _filteredUsers = MutableStateFlow<List<UserInfoResponse>>(emptyList())
    val users: StateFlow<List<UserInfoResponse>> = _filteredUsers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.get_users_info()
                _allUsers.value = result
                _filteredUsers.value = result
            } catch (e: Exception) {
                _allUsers.value = emptyList()
                _filteredUsers.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterUsers(query: String) {
        val lowerQuery = query.lowercase().trim()
        _filteredUsers.value = _allUsers.value.filter {
            it.primer_nombre?.lowercase()?.contains(lowerQuery) == true ||
                    it.apellido_pat?.lowercase()?.contains(lowerQuery) == true ||
                    it.usuario?.lowercase()?.contains(lowerQuery) == true
        }
    }
}
