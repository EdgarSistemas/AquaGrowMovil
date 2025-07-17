package com.example.aquagrow.ui.unit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.repository.UnitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UnitDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<UnitDetailState>(UnitDetailState.Loading)
    val state: StateFlow<UnitDetailState> = _state

    fun loadUnitDetail(unitId: Int) {
        viewModelScope.launch {
            try {
                _state.value = UnitDetailState.Loading
                val unit = UnitRepository().getUnitById(unitId)
                _state.value = UnitDetailState.Success(unit)
            } catch (e: Exception) {
                _state.value = UnitDetailState.Error("Error al cargar datos de la unidad")
            }
        }
    }
}