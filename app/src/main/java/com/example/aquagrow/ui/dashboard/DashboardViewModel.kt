package com.example.aquagrow.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.model.domain.Telemetry
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.example.aquagrow.data.repository.UnitRepository
import com.example.aquagrow.ui.assignUnit.AdminUnitViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import org.json.JSONObject

class DashboardViewModel(
    private val repository: UnitRepository = UnitRepository()
) : ViewModel() {

    private val _selectedUnit = MutableLiveData<com.example.aquagrow.data.model.domain.Unit?>()
    val selectedUnit: LiveData<Unit?> = _selectedUnit

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state

    private val _telemetries = MutableStateFlow<Map<Int, Telemetry>>(emptyMap())
    val telemetries: StateFlow<Map<Int, Telemetry>> = _telemetries

    private var isSubscribed = false

    init {
        Log.d("DashboardVM", "Inicializando ViewModel Dashboard")
        loadUserUnits()
        observeTelemetry()
    }

    fun loadUserUnits() {
        viewModelScope.launch {
            try {
                _state.value = DashboardState.Loading

                val tipoUsuario = SessionManager.getUserType()
                val units = if (tipoUsuario == "Administrador") {
                    repository.getAllUnitsWithZoneTankUser()
                } else {
                    repository.getUnitsForCurrentUser()
                }

                if (units.isEmpty()) {
                    _state.value = DashboardState.Empty
                } else {
                    _state.value = DashboardState.Success(units)

                    if (!isSubscribed) {
                        // Suscribirse a los topics MQTT
                        units.forEach { unit ->
                            val idUnidad = unit.id_unidad
                            val idDispositivo = unit.dispositivo?.id_dispositivo

                            if (idDispositivo != null) {
                                val topic = "invernadero/$idUnidad/$idDispositivo/telemetry"
                                MqttClientManager.subscribe(topic)
                                Log.d("DashboardVM", "✅ Suscrito a $topic")
                            } else {
                                Log.w("DashboardVM", "⚠️ Dispositivo nulo para unidad $idUnidad")
                            }
                        }
                        isSubscribed = true
                    }
                }
            } catch (e: Exception) {
                _state.value = DashboardState.Error("Error al cargar unidades: ${e.message}")
            }
        }
    }

    fun ensureMqttTelemetryObserver() {
        MqttCallbackBus.register { topic, payload ->
            if (topic.endsWith("/telemetry")) {
                val unidadId = topic.split("/").getOrNull(1)?.toIntOrNull() ?: return@register
                try {
                    val json = JSONObject(payload)
                    val lectura = Telemetry(
                        tempAgua = json.optDouble("tempAgua", -1.0),
                        tempZona = json.optDouble("tempZona", -1.0),
                        ph = json.optDouble("ph", -1.0)
                    )
                    _telemetries.value = _telemetries.value.toMutableMap().apply {
                        put(unidadId, lectura)
                    }
                } catch (e: Exception) {
                    Log.e("DashboardVM", "Error al parsear telemetría: $payload", e)
                }
            }
        }
    }

    private fun observeTelemetry() {
        Log.d("DashboardVM", "⏳ Registrando escucha de MQTT")
        MqttCallbackBus.register { topic, payload ->
            Log.d("DashboardVM", "MQTT recibido [$topic]: $payload")

            if (topic.contains("/telemetry")) {
                val parts = topic.split("/")
                val unidadId = parts.getOrNull(1)?.toIntOrNull() ?: return@register

                try {
                    val json = JSONObject(payload)
                    val tempAgua = json.optDouble("tempAgua", Double.NaN)
                    val tempZona = json.optDouble("tempZona", Double.NaN)
                    val ph = json.optDouble("ph", Double.NaN)
                    if (tempZona.isNaN() || tempAgua.isNaN() || ph.isNaN()) {
                        Log.e("DashboarVM", "Los valores de telemetría no son números")
                    } else {
                        val lectura = Telemetry(tempAgua, tempZona, ph)

                        _telemetries.value = _telemetries.value.toMutableMap().apply {
                            put(unidadId, lectura)
                        }

                        Log.d("DashboardVM", "Telemetría actualizada para unidad $unidadId")
                    }
                } catch (e: Exception) {
                    Log.e("DashboardVM", "Error al parsear telemetría: $payload", e)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        MqttCallbackBus.register { topic, payload ->  }
    }

    fun selectUnit(unit: com.example.aquagrow.data.model.domain.Unit) {
            _selectedUnit.value = unit
    }
}