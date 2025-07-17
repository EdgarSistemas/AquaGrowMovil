package com.example.aquagrow.data.remote.mqtt

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth
import java.nio.charset.StandardCharsets

object MqttClientManager {
    private var mqttClient: Mqtt3AsyncClient? = null

    // private const val BROKER_HOST = "4e1f320a15b442f09cbd54df897c263d.s1.eu.hivemq.cloud"
    private const val BROKER_HOST = "8cc34711662e4d5f82972c682f00e961.s1.eu.hivemq.cloud "
    private const val BROKER_PORT = 8883
    private const val USERNAME = "Jose_2003"
    private const val PASSWORD = "Jose_2003"

    fun connect() {
        if (mqttClient != null && mqttClient?.state?.isConnected == true) {
            Log.d("MQTT", "Ya hay una conexión MQTT activa. No se reconectará.")
            return
        }

        val simpleAuth = Mqtt3SimpleAuth.builder()
            .username(USERNAME)
            .password(PASSWORD.toByteArray())
            .build()

        mqttClient = MqttClient.builder()
            .useMqttVersion3()
            .identifier("aquagrow-android-${System.currentTimeMillis()}")
            .serverHost(BROKER_HOST)
            .serverPort(BROKER_PORT)
            .useSslWithDefaultConfig()
            .simpleAuth(simpleAuth)
            .buildAsync()

        mqttClient?.connect()?.whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e("MQTT", "Error al conectar con HiveMQ Cloud", throwable)
            } else {
                Log.d("MQTT", "Conectado a HiveMQ Cloud")

                mqttClient?.publishes(MqttGlobalPublishFilter.ALL) { publish ->
                    val topic = publish.topic.toString()
                    publish.payload.ifPresent { buffer ->
                        val payload = StandardCharsets.UTF_8.decode(buffer).toString()
                        Log.d("MQTT", "[PUB] $topic => $payload")
                        MqttCallbackBus.dispatch(topic, payload)
                    }
                }
            }
        }
    }

    fun subscribe(topic: String) {
        if (mqttClient == null && mqttClient?.state?.isConnected != true) {
            Log.e("MQTT", "Cliente MQTT no conectado, no puede suscribirse a $topic")
            return
        }

        mqttClient?.subscribeWith()
            ?.topicFilter(topic)
            ?.callback { publish ->
                val topic = publish.topic.toString()
                publish.payload.ifPresent { buffer ->
                    val payload = StandardCharsets.UTF_8.decode(buffer).toString()
                    Log.d("MQTT", "[SUB] $topic => $payload")
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
        Log.d("MQTT", "[PUB] $topic => $payload")
    }

    fun isConnected () : Boolean {
        return mqttClient != null && mqttClient?.state?.isConnected == true
    }

    fun disconnect() {
        mqttClient?.disconnect()
        mqttClient = null
    }
}
