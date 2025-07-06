package com.example.aquagrow.ui.user.form

import com.example.aquagrow.data.model.domain.UserType
import com.example.aquagrow.data.model.responses.UserComReponse

sealed class UserFormState {
    object Loading : UserFormState()
    data class UserLoaded(val user: UserComReponse) : UserFormState()
    data class TypesLoaded(val types: List<UserType>) : UserFormState()
    object CreateSuccess : UserFormState()
    object UpdateSuccess : UserFormState()
    data class Error(val message: String) : UserFormState()
}