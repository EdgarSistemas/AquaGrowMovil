object MqttCallbackBus {
    private val listeners = mutableSetOf<(String, String) -> Unit>()

    fun register(listener: (String, String) -> Unit) {
        listeners.add(listener)
    }

    fun unregister(listener: (String, String) -> Unit) {
        listeners.remove(listener)
    }

    fun dispatch(topic: String, payload: String) {
        listeners.forEach { it(topic, payload) }
    }
}
