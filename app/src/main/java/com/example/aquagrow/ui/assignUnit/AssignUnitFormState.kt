package com.example.aquagrow.ui.assignUnit

import com.example.aquagrow.data.model.domain.Unit

sealed class AssignUnitFormState {
    object Loading : AssignUnitFormState()
    data class Loaded(val unit: Unit) : AssignUnitFormState()
    object UpdateSuccess : AssignUnitFormState()
    object RemoveUserSuccess : AssignUnitFormState()
    data class Error(val message: String) : AssignUnitFormState()
}