package com.example.sistemariego1.data.remote

import android.content.Context
import android.util.Log
import com.example.sistemariego1.data.model.IrrigationData
import com.example.sistemariego1.data.model.MqttConfig
import com.example.sistemariego1.data.model.MqttConnectionState
import com.example.sistemariego1.data.model.PumpControlCommand
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttManager(private val context: Context) {
    private var client: MqttAsyncClient? = null
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private val _sensorData = MutableStateFlow(IrrigationData())
    val sensorData: StateFlow<IrrigationData> = _sensorData.asStateFlow()

    private var currentConfig = MqttConfig()

    fun connect(config: MqttConfig) {
        currentConfig = config
        scope.launch {
            try {
                _connectionState.value = MqttConnectionState.Connecting
                
                // Si existe un cliente previo, desconectar y limpiar
                client?.let {
                    if (it.isConnected) {
                        try { it.disconnect() } catch (_: Exception) {}
                    }
                    try { it.close() } catch (_: Exception) {}
                }

                client = MqttAsyncClient(
                    config.serverUri,
                    config.clientId,
                    MemoryPersistence()
                )

                client?.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        Log.e("MqttManager", "Conexión perdida: ${cause?.message}")
                        _connectionState.value = MqttConnectionState.Disconnected
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        if (message != null && topic == currentConfig.topicSensores) {
                            val jsonString = String(message.payload)
                            Log.d("MqttManager", "Mensaje recibido en $topic: $jsonString")
                            try {
                                val data = gson.fromJson(jsonString, IrrigationData::class.java)
                                if (data != null) {
                                    _sensorData.value = data
                                }
                            } catch (e: Exception) {
                                Log.e("MqttManager", "Error al parsear JSON: ${e.message}")
                            }
                        }
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d("MqttManager", "Entrega completada")
                    }
                })

                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    connectionTimeout = 10
                    keepAliveInterval = 30
                }

                client?.connect(options, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MqttManager", "Conexión exitosa a ${config.serverUri}")
                        _connectionState.value = MqttConnectionState.Connected
                        subscribeToTopics()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.e("MqttManager", "Fallo al conectar: ${exception?.message}")
                        _connectionState.value = MqttConnectionState.Error(
                            exception?.localizedMessage ?: "Fallo al conectar con el servidor MQTT"
                        )
                    }
                })
            } catch (e: Exception) {
                Log.e("MqttManager", "Excepción en connect: ${e.message}")
                _connectionState.value = MqttConnectionState.Error(
                    e.localizedMessage ?: "Excepción en el cliente MQTT"
                )
            }
        }
    }

    private fun subscribeToTopics() {
        try {
            if (client?.isConnected == true) {
                client?.subscribe(currentConfig.topicSensores, 1)
                Log.d("MqttManager", "Suscrito al tópico: ${currentConfig.topicSensores}")
            }
        } catch (e: Exception) {
            Log.e("MqttManager", "Error al suscribir: ${e.message}")
        }
    }

    fun togglePump(turnOn: Boolean) {
        val payload = gson.toJson(PumpControlCommand(bomba = if (turnOn) "ON" else "OFF"))
        publish(currentConfig.topicBombaControl, payload)
        
        // Actualización optimista del estado local de la UI
        val current = _sensorData.value
        _sensorData.value = current.copy(bombaStateStr = if (turnOn) "ON" else "OFF")
    }

    fun publish(topic: String, messageStr: String) {
        scope.launch {
            try {
                if (client?.isConnected == true) {
                    val message = MqttMessage(messageStr.toByteArray()).apply {
                        qos = 1
                    }
                    client?.publish(topic, message)
                    Log.d("MqttManager", "Publicado en $topic: $messageStr")
                } else {
                    Log.w("MqttManager", "No se puede publicar: Cliente no conectado")
                }
            } catch (e: Exception) {
                Log.e("MqttManager", "Error al publicar: ${e.message}")
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try {
                if (client?.isConnected == true) {
                    client?.disconnect()
                }
                _connectionState.value = MqttConnectionState.Disconnected
                Log.d("MqttManager", "Desconectado de MQTT")
            } catch (e: Exception) {
                Log.e("MqttManager", "Error al desconectar: ${e.message}")
            }
        }
    }
}
