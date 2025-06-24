package com.example.aquagrow.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val authResponse = authRepository.login(username, password)
                _loginState.value = LoginState.Success(authResponse.user)
            } catch (e: HttpException) {
                Log.e("LoginVM", "Error capturado: ${e.message}", e)
                // Error HTTP (401, 500, etc.)
                _loginState.value = LoginState.Error("Error ${e.code()}: ${e.message()}")
            } catch (e: IOException) {
                Log.e("LoginVM", "Error capturado: ${e.message}", e)
                // Error de red
                _loginState.value = LoginState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                Log.e("LoginVM", "Error capturado: ${e.message}", e)
                // Error general
                _loginState.value = LoginState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }
}