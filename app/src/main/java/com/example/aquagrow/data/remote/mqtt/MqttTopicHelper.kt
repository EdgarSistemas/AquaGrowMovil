package com.example.aquagrow.data.remote.mqtt

object MqttTopicHelper {
    fun extractUnidadId(topic: String): Int {
        return topic.split("/").getOrNull(1)?.toIntOrNull() ?: -1
    }

    fun extractDispositivoId(topic: String): Int {
        return topic.split("/").getOrNull(2)?.toIntOrNull() ?: -1
    }
}
