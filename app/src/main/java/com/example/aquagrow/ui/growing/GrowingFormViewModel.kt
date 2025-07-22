package com.example.aquagrow.ui.growing

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.repository.GrowingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GrowingFormViewModel : ViewModel() {

    private val _state = MutableStateFlow<GrowingFormState>(GrowingFormState.Loading)
    val state: StateFlow<GrowingFormState> = _state

    private val repository = GrowingRepository()

    fun cargarCultivo(unitId: Int) {
        viewModelScope.launch {
            _state.value = GrowingFormState.Loading
            try {
                val growing = repository.get_growing_by_unit_id(unitId)
                _state.value = GrowingFormState.Form(growing)
            } catch (e: Exception) {
                Log.e("GrowingViewModel", "No hay cultivo activo o error: ${e.message}")
                _state.value = GrowingFormState.Form(null)
            }
        }
    }

    fun guardarNuevoCultivo(unitId: Int, nombre: String, dias: Int, rendimiento: Int) {
        viewModelScope.launch {
            _state.value = GrowingFormState.Loading
            try {
                val req = GrowingAddRequest(unitId, nombre, dias, rendimiento)
                repository.create_growing(req)
                _state.value = GrowingFormState.Success
            } catch (e: Exception) {
                _state.value = GrowingFormState.Error("Error al crear cultivo")
            }
        }
    }

    fun actualizarCultivo(unitId: Int, idCultivo: Int, nombre: String, dias: Int, rendimiento: Int) {
        viewModelScope.launch {
            _state.value = GrowingFormState.Loading
            try {
                val req = GrowingUpdRequest(unitId, idCultivo, nombre, dias, rendimiento)
                repository.update_growing(req)
                _state.value = GrowingFormState.Success
            } catch (e: Exception) {
                _state.value = GrowingFormState.Error("Error al actualizar cultivo")
            }
        }
    }

    fun terminarCultivo(unitId: Int, idCultivo: Int, motivo: String, diasReal: Int, rendimientoReal: Int, estatusFinal: Int) {
        viewModelScope.launch {
            _state.value = GrowingFormState.Loading
            try {
                val req = GrowingFinishRequest(unitId, idCultivo, motivo, diasReal, rendimientoReal, estatusFinal)
                repository.terminate_growing(req)
                _state.value = GrowingFormState.Success
            } catch (e: Exception) {
                _state.value = GrowingFormState.Error("Error al terminar cultivo")
            }
        }
    }
}
