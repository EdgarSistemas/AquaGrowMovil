package com.example.aquagrow.ui.user.list

import com.example.aquagrow.data.model.responses.UserComReponse

sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<UserComReponse>) : UserListState()
    data class Error(val message: String) : UserListState()
    object Empty : UserListState()
    data class DeleteSuccess(val userId: Int) : UserListState()
}