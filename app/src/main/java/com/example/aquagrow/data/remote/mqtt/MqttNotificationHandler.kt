package com.example.aquagrow.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.aquagrow.R
import com.example.aquagrow.data.remote.mqtt.MqttTopicHelper
import org.json.JSONObject
import com.example.aquagrow.util.UnidadCache

object MqttNotificationHandler {
    fun initGlobalAlertSubscription(context: Context) {
        MqttCallbackBus.register { topic, payload ->
            if (topic.endsWith("/alert")) {
                val unidadId = MqttTopicHelper.extractUnidadId(topic)
                val json = JSONObject(payload)
                val tipo = json.optString("tipo", "Alerta sin detalles")

                // Mensaje personalizado por tipo de alerta
                val mensaje = when (tipo.lowercase()) {
                    "alerta_temp" -> {
                        val tempZona = json.optDouble("tempZona", Double.NaN)
                        "Temperatura ambiental fuera de rango: ${"%.1f".format(tempZona)} °C"
                    }
                    "temp_agua fuera de rango" -> {
                        val valor = json.optDouble("valor", Double.NaN)
                        "Temperatura del agua fuera de rango: ${"%.1f".format(valor)} °C"
                    }
                    "nivel_agua fuera de rango" -> {
                        val valor = json.optDouble("valor", Double.NaN)
                        "Nivel de agua fuera de rango: ${"%.1f".format(valor)} litros"
                    }
                    else -> tipo
                }

                showAlertNotification(context, unidadId, mensaje)
            }
        }
    }

    private fun showAlertNotification(context: Context, unidadId: Int, mensaje: String) {
        val channelId = "alert_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Unidades",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val nombreUnidad = UnidadCache.getNombre(unidadId)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_warning_24)
            .setContentTitle("Alerta en unidad $nombreUnidad")
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}