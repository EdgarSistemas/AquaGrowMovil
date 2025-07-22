package com.example.aquagrow.ui.configTank

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.data.model.requests.*
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.example.aquagrow.data.repository.TankConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class TankConfigViewModel(
    private val repository: TankConfigRepository,
    private val mqttManager: MqttClientManager
) : ViewModel() {

    private val _state = MutableStateFlow<TankConfigState>(TankConfigState.Loading)
    val state: StateFlow<TankConfigState> = _state

    private var estanqueId: Int = -1
    private var unidadId: Int = -1
    private var dispId: Int = -1
    private var idConfigTank: Int? = null
    private var idFeedingSchedule: Int? = null

    fun setIdentifiers(estanqueId: Int, programacionId: Int?, unidadId: Int, dispId: Int) {
        this.estanqueId = estanqueId
        this.unidadId = unidadId
        this.dispId = dispId
        this.idFeedingSchedule = programacionId
    }

    fun loadData(estanqueId: Int?, programacionId: Int?) {
        viewModelScope.launch {
            _state.value = TankConfigState.Loading
            try {
                val config = estanqueId?.let {
                    repository.getConfigTank(it)
                }
                idConfigTank = config?.id_config

                val feeding = programacionId?.let {
                    idFeedingSchedule = it
                    repository.getFeedingSchedule(it)
                }

                _state.value = TankConfigState.Loaded(
                    configTank = config,
                    feedingSchedule = feeding,
                    isExistingConfig = config != null,
                    isExistingSchedule = feeding != null
                )

            } catch (e: Exception) {
                _state.value = TankConfigState.Error("Error al cargar datos: ${e.message}")
            }
        }
    }

    fun saveConfigTank(
        tempMin: Double, tempMax: Double,
        phMin: Double, phMax: Double,
        distMin: Double, distMax: Double
    ) {
        viewModelScope.launch {
            try {
                val response = if (idConfigTank != null) {
                    repository.updateConfigTank(
                        ConfigTankUpdRequest(
                            id_config = idConfigTank!!,
                            temp_agua_min = tempMin,
                            temp_agua_max = tempMax,
                            ph_min = phMin,
                            ph_max = phMax,
                            dist_min = distMin,
                            dist_max = distMax
                        )
                    )
                } else {
                    repository.createConfigTank(
                        ConfigTankAddRequest(
                            estanque_id = estanqueId,
                            temp_agua_min = tempMin,
                            temp_agua_max = tempMax,
                            ph_min = phMin,
                            ph_max = phMax,
                            dist_min = distMin,
                            dist_max = distMax
                        )
                    )
                }

                publishMqttTankConfig(tempMin, tempMax, phMin, phMax, distMin, distMax)

                _state.value = TankConfigState.Success(response.mensaje)
            } catch (e: Exception) {
                _state.value = TankConfigState.Error("Error al guardar configuración: ${e.message}")
            }
        }
    }

    private fun publishMqttTankConfig(
        tempMin: Double, tempMax: Double,
        phMin: Double, phMax: Double,
        distMin: Double, distMax: Double
    ) {
        val topic = "invernadero/$unidadId/$dispId/config/set"
        val payload = JSONObject().apply {
            put("tipo", "tank")
            put("tank", JSONObject().apply {
                put("temp_agua_min", tempMin)
                put("temp_agua_max", tempMax)
                put("ph_min", phMin)
                put("ph_max", phMax)
                put("dist_min", distMin)
                put("dist_max", distMax)
            })
        }.toString()
        Log.d("TCViewModel", "MQTT payload tank config: $payload")
        mqttManager.publish(topic, payload)
    }

    fun saveFeedingSchedule(
        horaInicio: String,
        duracionMinutos: Int,
        tipoFrecuencia: String,
        diasSemana: String?,
        frecuenciaDia: Int?,
        intervaloMinutos: Int?
    ) {
        viewModelScope.launch {
            try {
                val response = if (idFeedingSchedule != null) {
                    repository.updateFeedingSchedule(
                        FeedingScheduleUpdRequest(
                            id_programacion = idFeedingSchedule!!,
                            hora_inicio = horaInicio,
                            duracion_minutos = duracionMinutos,
                            tipo_frecuencia = tipoFrecuencia,
                            dias_semana = diasSemana,
                            frecuencia_dia = frecuenciaDia,
                            intervalo_minutos = intervaloMinutos
                        )
                    )
                } else {
                    repository.createFeedingSchedule(
                        FeedingScheduleAddRequest(
                            estanque_id = estanqueId,
                            hora_inicio = horaInicio,
                            duracion_minutos = duracionMinutos,
                            tipo_frecuencia = tipoFrecuencia,
                            dias_semana = diasSemana,
                            frecuencia_dia = frecuenciaDia,
                            intervalo_minutos = intervaloMinutos
                        )
                    )
                }

                publishMqttFeeding(
                    horaInicio, duracionMinutos, tipoFrecuencia,
                    diasSemana, frecuenciaDia, intervaloMinutos
                )

                _state.value = TankConfigState.Success(response.mensaje)
            } catch (e: Exception) {
                _state.value = TankConfigState.Error("Error al guardar programación: ${e.message}")
            }
        }
    }

    private fun publishMqttFeeding(
        horaInicio: String,
        duracionMinutos: Int,
        tipoFrecuencia: String,
        diasSemana: String?,
        frecuenciaDia: Int?,
        intervaloMinutos: Int?
    ) {
        val topic = "invernadero/$unidadId/$dispId/config/set"
        val payload = JSONObject().apply {
            put("tipo", "feeding")
            put("feeding", JSONObject().apply {
                put("hora_inicio", horaInicio)
                put("duracion_minutos", duracionMinutos)
                put("tipo_frecuencia", tipoFrecuencia)
                put("dias_semana", diasSemana ?: "")
                put("frecuencia_dia", frecuenciaDia)
                put("intervalo_minutos", intervaloMinutos)
            })
        }.toString()
        Log.d("TCViewModel", "MQTT payload feeding: $payload")
        mqttManager.publish(topic, payload)
    }

    fun activateActuator(actuator: String, isChecked: Boolean) {
        val topic = "invernadero/$unidadId/$dispId/command"
        val payload = """{"actuador":"$actuator", "estado":"$isChecked"}"""
        Log.d("TCViewModel", "MQTT actuator command: $payload")

        if (unidadId != -1 && dispId != -1) {
            mqttManager.publish(topic, payload)
        } else {
            Log.e("TCViewModel", "MQTT ERROR: unidadId o dispId no establecidos")
        }
    }

    fun subscribeToTelemetry(tvTempAgua: TextView, tvPhAgua: TextView, tvNivelAgua: TextView) {
        val topic = "invernadero/$unidadId/$dispId/telemetry"
        mqttManager.subscribe(topic)

        MqttCallbackBus.register { receivedTopic, payload ->
            if (receivedTopic == topic) {
                try {
                    val json = org.json.JSONObject(payload)
                    val temp = json.optDouble("tempAgua", Double.NaN)
                    val ph = json.optDouble("ph", Double.NaN)
                    val nivel = json.optDouble("distancia", Double.NaN)
                    if (!temp.isNaN()) {
                        tvTempAgua.post {
                            tvTempAgua.text = "Temp. estanque: ${"%.1f".format(temp)} °C"
                        }
                    }
                    if (!ph.isNaN()) {
                        tvPhAgua.post{
                            tvPhAgua.text = "pH del estanque: ${"%.1f".format(ph)}"
                        }
                    }
                    if (!nivel.isNaN()) {
                        tvNivelAgua.post{
                            tvNivelAgua.text = "Nivel de agua: ${"%.1f".format(nivel)} L"
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
            Log.e("TankConfigViewModel", "Error obteniendo unidad completa", e)
            null
        }
    }
}
