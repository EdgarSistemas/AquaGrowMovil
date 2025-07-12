package com.example.aquagrow.ui.assignUnit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.repository.UnitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class AdminUnitViewModel(
    private val repository: UnitRepository = UnitRepository()
) : ViewModel() {

    private val _units = MutableStateFlow<List<Unit>>(emptyList())
    val units: StateFlow<List<Unit>> = _units

    private val _selectedUnit = MutableStateFlow<Unit?>(null)
    val selectedUnit: StateFlow<Unit?> = _selectedUnit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadAllUnits()
    }

    fun loadAllUnits() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllUnitsWithZoneTankUser()
                _units.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("AdminUnitVM", "Error cargando unidades", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectUnit(unit: Unit) {
        _selectedUnit.value = unit
    }

    fun clearSelection() {
        _selectedUnit.value = null
    }
}
