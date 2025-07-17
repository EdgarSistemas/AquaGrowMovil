package com.example.aquagrow.ui.zone

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.example.aquagrow.data.repository.ConfigZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ZoneConfigViewModel(
    private val repository: ConfigZoneRepository,
    private val mqttManager: MqttClientManager
) : ViewModel() {

    private val _state = MutableStateFlow<ZoneConfigState>(ZoneConfigState.Loading)
    val state: StateFlow<ZoneConfigState> = _state

    private var zonaId: Int = -1
    private var unidadId: Int = -1
    private var dispId: Int = -1

    private var idConfigZone: Int? = null
    private var idProgramacion: Int? = null

    fun setIdentifiers(zonaId: Int, unidadId: Int, dispId: Int) {
        this.zonaId = zonaId
        this.unidadId = unidadId
        this.dispId = dispId
    }

    fun loadData(zonaId: Int?, programacionId: Int?) {
        viewModelScope.launch {
            _state.value = ZoneConfigState.Loading
            try {
                val configZone = zonaId?.let {
                    idConfigZone = it
                    repository.getConfigZone(it)  // ← nombre correcto
                }

                val programacion = programacionId?.let {
                    idProgramacion = it
                    repository.getIrrigationSchedule(it)  // ← nombre correcto
                }

                _state.value = ZoneConfigState.Loaded(
                    configZone = configZone,
                    programacionRiego = programacion,
                    isExistingConfig = configZone != null,
                    isExistingSchedule = programacion != null
                )

            } catch (e: Exception) {
                _state.value = ZoneConfigState.Error("Error al cargar datos: ${e.message}")
            }
        }
    }

    fun saveConfigZone(tempMin: Double, tempMax: Double) {
        viewModelScope.launch {
            try {
                val response = if (idConfigZone != null) {
                    repository.updateConfigZone(ConfigZoneUpdRequest(idConfigZone!!, tempMin, tempMax))
                } else {
                    repository.createConfigZone(ConfigZoneAddRequest(zonaId, tempMin, tempMax))
                }

                publishMqttConfig(tempMin, tempMax)

                _state.value = ZoneConfigState.Success(response.mensaje)
            } catch (e: Exception) {
                _state.value = ZoneConfigState.Error("Error al guardar configuración: ${e.message}")
            }
        }
    }

    fun saveIrrigationSchedule(
        horaInicio: String,
        duracionMinutos: Int,
        tipoFrecuencia: String,
        diasSemana: String,
        frecuenciaDia: Int,
        intervaloMinutos: Int
    ) {
        viewModelScope.launch {
            try {
                val response = if (idProgramacion != null) {
                    repository.updateIrrigationSchedule(
                        IrrigationUpdRequest(
                            id_programacion = idProgramacion!!,
                            hora_inicio = horaInicio,
                            duracion_minutos = duracionMinutos,
                            tipo_frecuencia = tipoFrecuencia,
                            dias_semana = diasSemana,
                            frecuencia_dia = frecuenciaDia,
                            intervalo_minutos = intervaloMinutos
                        )
                    )
                } else {
                    repository.createIrrigationSchedule(
                        IrrigationAddRequest(
                            zona_id = zonaId,
                            hora_inicio = horaInicio,
                            duracion_minutos = duracionMinutos,
                            tipo_frecuencia = tipoFrecuencia,
                            dias_semana = diasSemana,
                            frecuencia_dia = frecuenciaDia,
                            intervalo_minutos = intervaloMinutos
                        )
                    )
                }

                publishMqttIrrigation(
                    horaInicio, duracionMinutos, tipoFrecuencia,
                    diasSemana, frecuenciaDia, intervaloMinutos
                )

                _state.value = ZoneConfigState.Success(response.mensaje)
            } catch (e: Exception) {
                _state.value = ZoneConfigState.Error("Error al guardar programación: ${e.message}")
            }
        }
    }

    fun activateActuator(actuator: String, isChecked : Boolean) {
        val topic = "invernadero/$unidadId/$dispId/command"
        val payload = """{"actuador":"$actuator", "estado":"$isChecked"}"""
        Log.e("ZCViewModel", "payload de actuador: $payload")
        mqttManager.publish(topic, payload)
    }

    private fun publishMqttConfig(tempMin: Double, tempMax: Double) {
        val topic = "invernadero/$unidadId/$dispId/config/set"
        val payload = """{
            "tipo": "zona",
            "temp_min": $tempMin,
            "temp_max": $tempMax
        }""".trimIndent()
        Log.e("ZCViewModel", "payload de confg zone $payload")
        mqttManager.publish(topic, payload)
    }

    private fun publishMqttIrrigation(
        horaInicio: String,
        duracionMinutos: Int,
        tipoFrecuencia: String,
        diasSemana: String,
        frecuenciaDia: Int,
        intervaloMinutos: Int
    ) {
        val topic = "invernadero/$unidadId/$dispId/config/set"
        val payload = """{
            "tipo": "zona",
            "irrigation": {
                "hora_inicio": "$horaInicio",
                "duracion_minutos": $duracionMinutos,
                "tipo_frecuencia": "$tipoFrecuencia",
                "dias_semana": "$diasSemana",
                "frecuencia_dia": $frecuenciaDia,
                "intervalo_minutos": $intervaloMinutos
            }
        }""".trimIndent()
        Log.e("ZCViewModel", "payload de programacion irrigation $payload")

        mqttManager.publish(topic, payload)
    }

    fun subscribeToTelemetry(tvTempZona: TextView) {
        val topic = "invernadero/$unidadId/$dispId/telemetry"

        mqttManager.subscribe(topic)

        MqttCallbackBus.register { receivedTopic, payload ->
            if (receivedTopic == topic) {
                try {
                    val json = org.json.JSONObject(payload)
                    val temp = json.optDouble("tempZona", Double.NaN)
                    if (!temp.isNaN()) {
                        tvTempZona.post {
                            tvTempZona.text = "Temp. zona: ${"%.1f".format(temp)} °C"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getFullUnitFromApi(unitId: Int): Unit? {
        return try {
            repository.getUnitByIdWithZoneTankUser(unitId)
        } catch (e: Exception) {
            Log.e("ZoneConfigViewModel", "Error obteniendo unidad completa", e)
            null
        }
    }
}
