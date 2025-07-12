package com.example.aquagrow.ui.assignUnit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.domain.User
import com.example.aquagrow.data.model.responses.UserInfoResponse
import com.example.aquagrow.data.repository.UnitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AssignUnitFormViewModel(
    private val repository: UnitRepository = UnitRepository()
) : ViewModel() {

    // Estado observable del formulario
    private val _state = MutableStateFlow<AssignUnitFormState>(AssignUnitFormState.Loading)
    val state: StateFlow<AssignUnitFormState> = _state

    // Unidad actualmente editada
    private var _currentUnit: Unit? = null
    val currentUnit: Unit? get() = _currentUnit

    fun loadUnit(unit: Unit) {
        _currentUnit = unit
        _state.value = AssignUnitFormState.Loaded(unit)
    }

    fun updateUnit(nombreUnidad: String, nombreZona: String, nombreEstanque: String) {
        val unidad = _currentUnit ?: return
        _state.value = AssignUnitFormState.Loading

        viewModelScope.launch {
            try {
                repository.updateUnitValues(
                    idUnidad = unidad.id_unidad,
                    nombreUnidad = nombreUnidad,
                    nombreZona = nombreZona,
                    nombreEstanque = nombreEstanque
                )
                _currentUnit = unidad.copy(
                    nombre = nombreUnidad,
                    zone = unidad.zone?.copy(nombre = nombreZona),
                    tank = unidad.tank?.copy(nombre = nombreEstanque),
                    user = unidad.user
                )
                _state.value = AssignUnitFormState.UpdateSuccess
            } catch (e: Exception) {
                _state.value = AssignUnitFormState.Error(e.message ?: "Error desconocido al actualizar unidad")
            }
        }
    }

    fun removeUser() {
        val unidad = _currentUnit ?: return
        _state.value = AssignUnitFormState.Loading

        viewModelScope.launch {
            try {
                repository.removeUserFromUnit(unidad.id_unidad)
                _currentUnit = unidad.copy(user = null)
                _state.value = AssignUnitFormState.RemoveUserSuccess
            } catch (e: Exception) {
                _state.value = AssignUnitFormState.Error(e.message ?: "Error al quitar usuario")
            }
        }
    }

    fun assignUser(user : UserInfoResponse) {
        val unidad = currentUnit ?: return
        _state.value = AssignUnitFormState.Loading

        viewModelScope.launch {
            try {
                repository.assignUserToUnit(unidad.id_unidad, user.id_usuario!!)
                var usuario = User(id_usuario = user.id_usuario,
                    primer_nombre = user.primer_nombre!!, apellido_pat = user.apellido_pat!!,
                    usuario = user.usuario!!, estatus = user.estatus!!,
                    fecha_ultima_conexion = null, tipo_usuario = null,
                    permisos = null)
                val updated = unidad.copy(user = usuario)
                _currentUnit = updated
                _state.value = AssignUnitFormState.Loaded(updated)
            } catch (e: Exception) {
                _state.value = AssignUnitFormState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
