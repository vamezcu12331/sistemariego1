package com.example.sistemariego1.data.model

/**
 * Configuración de conexión al Broker MQTT / Node-RED.
 */
data class MqttConfig(
    val brokerIp: String = "192.168.1.100",
    val port: Int = 1883,
    val clientId: String = "Android_Riego_App_" + System.currentTimeMillis().toString().takeLast(4),
    val topicSensores: String = "riego/sensores",
    val topicBombaControl: String = "riego/bomba/control"
) {
    val serverUri: String
        get() = "tcp://$brokerIp:$port"
}

/**
 * Estados posibles de la conexión MQTT.
 */
sealed class MqttConnectionState {
    object Disconnected : MqttConnectionState()
    object Connecting : MqttConnectionState()
    object Connected : MqttConnectionState()
    data class Error(val message: String) : MqttConnectionState()
}
