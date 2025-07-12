package com.example.aquagrow.ui.assignUnit.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.repository.UnitRepository
import kotlinx.coroutines.launch

class AdminUnitListViewModel(
    private val repository: UnitRepository = UnitRepository()
) : ViewModel() {

    private val _state = MutableLiveData<AdminUnitListState>()
    val state: LiveData<AdminUnitListState> = _state

    fun loadAllUnits() {
        viewModelScope.launch {
            try {
                _state.value = AdminUnitListState.Loading
                val units = repository.getAllUnitsWithZoneTankUser()
                _state.value = AdminUnitListState.Success(units)
            } catch (e: Exception) {
                _state.value = AdminUnitListState.Error("Error al cargar unidades: ${e.message}")
            }
        }
    }
}
