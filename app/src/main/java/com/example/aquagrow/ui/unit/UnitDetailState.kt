package com.example.aquagrow.ui.unit

import com.example.aquagrow.data.model.domain.Unit

sealed class UnitDetailState {
    object Loading : UnitDetailState()
    data class Success(val unit: Unit) : UnitDetailState()
    data class Error(val message: String) : UnitDetailState()
}