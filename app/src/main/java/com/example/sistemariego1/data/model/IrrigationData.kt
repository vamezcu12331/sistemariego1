package com.example.sistemariego1.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos recibido en formato JSON desde Node-RED a través de MQTT (ej. topic: riego/sensores).
 */
data class IrrigationData(
    @SerializedName("humedad_suelo")
    val humedadSuelo: Float = 45f,

    @SerializedName("temperatura")
    val temperatura: Float = 24.5f,

    @SerializedName("humedad_ambiente")
    val humedadAmbiente: Float = 60f,

    @SerializedName("lloviendo")
    val lloviendo: Boolean = false,

    @SerializedName("luz_lux")
    val luzLux: Float = 550f,

    @SerializedName("nivel_agua")
    val nivelAgua: Float = 75f,

    @SerializedName("bomba")
    val bombaStateStr: String = "OFF"
) {
    val bombaEncendida: Boolean
        get() = bombaStateStr.uppercase() == "ON"

    val estadoSuelo: SoilState
        get() = when {
            humedadSuelo < 30f -> SoilState.SECO
            humedadSuelo <= 70f -> SoilState.OPTIMO
            else -> SoilState.HUMEDO
        }

    val estadoLuz: LightState
        get() = when {
            luzLux < 200f -> LightState.NOCHE
            luzLux <= 600f -> LightState.NUBLADO
            else -> LightState.DIA
        }

    val nivelAguaCritico: Boolean
        get() = nivelAgua < 20f
}

enum class SoilState(val label: String) {
    SECO("Seco"),
    OPTIMO("Óptimo"),
    HUMEDO("Húmedo")
}

enum class LightState(val label: String) {
    NOCHE("Noche"),
    NUBLADO("Nublado"),
    DIA("Día")
}

/**
 * Modelo de comando enviado hacia Node-RED (ej. topic: riego/bomba/control).
 */
data class PumpControlCommand(
    @SerializedName("bomba")
    val bomba: String
)
