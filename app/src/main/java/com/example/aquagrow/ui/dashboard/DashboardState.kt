package com.example.aquagrow.ui.dashboard

import com.example.aquagrow.data.model.domain.Unit

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val units : List<Unit>) : DashboardState()
    data class Error(val message : String) : DashboardState()
    object Empty : DashboardState()
}