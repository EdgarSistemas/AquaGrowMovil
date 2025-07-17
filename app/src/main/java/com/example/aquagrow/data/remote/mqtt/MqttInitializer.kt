package com.example.aquagrow.data.remote.mqtt

import android.util.Log
import com.example.aquagrow.AquagrowApp
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.repository.UnitRepository
import com.example.aquagrow.ui.notifications.MqttNotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MqttInitializer {
    fun initAfterLogin() {
        Log.d("MQTT_INIT", "Iniciando MQTT post-login")

        MqttClientManager.connect()

        // Registrar alertas globales si aún no se han registrado
        if (!AquagrowApp.alertListenerRegistered) {
            MqttNotificationHandler.initGlobalAlertSubscription(AquagrowApp.instance.applicationContext)
            AquagrowApp.alertListenerRegistered = true
        }

        // Suscribirse a alertas de las unidades del usuario
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tipoUsuario = SessionManager.getUserType()
                val units = if (tipoUsuario == "Administrador") {
                    UnitRepository().getAllUnitsWithZoneTankUser()
                } else {
                    UnitRepository().getUnitsForCurrentUser()
                }

                units.forEach { unit ->
                    val dispId = unit.dispositivo?.id_dispositivo ?: return@forEach
                    val topic = "invernadero/${unit.id_unidad}/$dispId/alert"
                    MqttClientManager.subscribe(topic)
                    Log.d("MQTT_INIT", "Subscrito a $topic")
                }
            } catch (e: Exception) {
                Log.e("MQTT_INIT", "Error al suscribirse a tópicos MQTT", e)
            }
        }
    }
}