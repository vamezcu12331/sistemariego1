package com.example.sistemariego1.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sistemariego1.data.model.IrrigationData
import com.example.sistemariego1.data.model.MqttConfig
import com.example.sistemariego1.data.model.MqttConnectionState
import com.example.sistemariego1.data.remote.MqttManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val mqttManager = MqttManager(application.applicationContext)

    val connectionState: StateFlow<MqttConnectionState> = mqttManager.connectionState
    val sensorData: StateFlow<IrrigationData> = mqttManager.sensorData

    private val _mqttConfig = MutableStateFlow(MqttConfig())
    val mqttConfig: StateFlow<MqttConfig> = _mqttConfig.asStateFlow()

    init {
        // Conexión inicial automática con la configuración por defecto
        connectMqtt(_mqttConfig.value)
    }

    fun connectMqtt(config: MqttConfig) {
        _mqttConfig.value = config
        mqttManager.connect(config)
    }

    fun disconnectMqtt() {
        mqttManager.disconnect()
    }

    fun togglePump(turnOn: Boolean) {
        mqttManager.togglePump(turnOn)
    }
}
