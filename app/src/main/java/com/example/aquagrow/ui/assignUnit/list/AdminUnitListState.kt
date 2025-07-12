package com.example.aquagrow.ui.assignUnit.list

sealed class AdminUnitListState {
    object Loading : AdminUnitListState()
    data class Success(val units: List<com. example. aquagrow. data. model. domain.Unit>) : AdminUnitListState()
    data class Error(val message: String) : AdminUnitListState()
}
