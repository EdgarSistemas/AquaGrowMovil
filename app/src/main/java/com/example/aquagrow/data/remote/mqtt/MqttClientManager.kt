// Ubicación: com.example.aquagrow.data.remote.mqtt.MqttClientManager.kt
package com.example.aquagrow.data.remote.mqtt

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import java.nio.charset.StandardCharsets

object MqttClientManager {
    private var mqttClient: Mqtt3AsyncClient? = null

    fun connect(brokerHost: String = "192.168.0.100", port: Int = 1883) {
        if (mqttClient?.state?.isConnected == true) return

        mqttClient = MqttClient.builder()
            .useMqttVersion3()
            .identifier("aquagrow-android-${System.currentTimeMillis()}")
            .serverHost(brokerHost)
            .serverPort(port)
            .buildAsync()

        mqttClient?.connect()?.whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e("MQTT", "Fallo de conexión", throwable)
            } else {
                Log.d("MQTT", "Conectado al broker MQTT")

                mqttClient?.publishes(MqttGlobalPublishFilter.ALL) { publish ->
                    val topic = publish.topic.toString()
                    publish.payload.ifPresent { buffer ->
                        val payload = StandardCharsets.UTF_8.decode(buffer).toString()
                        Log.d("MQTT", "📥 Mensaje recibido [$topic]: $payload")
                        MqttCallbackBus.dispatch(topic, payload)
                    }
                }
            }
        }
    }

    fun subscribe(topic: String) {
        mqttClient?.subscribeWith()
            ?.topicFilter(topic)
            ?.callback { publish ->
                val topic = publish.topic.toString()
                publish.payload.ifPresent { buffer ->
                    val payload = StandardCharsets.UTF_8.decode(buffer).toString()
                    Log.d("MQTT", "📥 [SUB] $topic => $payload")
                    MqttCallbackBus.dispatch(topic, payload)
                }
            }
            ?.send()
    }

    fun publish(topic: String, payload: String) {
        mqttClient?.publishWith()
            ?.topic(topic)
            ?.payload(payload.toByteArray(StandardCharsets.UTF_8))
            ?.send()
        Log.d("MQTT", "📤 [PUB] $topic => $payload")
    }

    fun disconnect() {
        mqttClient?.disconnect()
        mqttClient = null
    }
}
