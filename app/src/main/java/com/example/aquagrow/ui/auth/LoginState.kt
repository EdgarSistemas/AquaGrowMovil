package com.example.aquagrow.ui.auth

import com.example.aquagrow.data.model.domain.User

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}