package com.example.aquagrow.ui.profile

import com.example.aquagrow.data.model.responses.UserInfoResponse

sealed class ProfileState {
    object Loading : ProfileState()
    object Success : ProfileState()
    data class Loaded(val user: UserInfoResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
