package com.example.aquagrow.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.aquagrow.R
import com.example.aquagrow.data.remote.mqtt.MqttTopicHelper
import org.json.JSONObject

object MqttNotificationHandler {
    fun initGlobalAlertSubscription(context: Context) {
        MqttCallbackBus.register { topic, payload ->
            if (topic.endsWith("/alert")) {
                val unidadId = MqttTopicHelper.extractUnidadId(topic)
                val json = JSONObject(payload)
                val tipo = json.optString("tipo", "Alerta sin detalles")
                val lectura = payload

                showAlertNotification(context, unidadId, tipo, lectura)
            }
        }
    }

    fun showAlertNotification(context: Context, unidadId: Int, mensaje: String, lectura: String) {
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

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_warning_24)
            .setContentTitle("Alerta en unidad $unidadId")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$mensaje\n\n$lectura"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(unidadId, notification)
    }
}