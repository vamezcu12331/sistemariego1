package com.example.sistemariego1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sistemariego1.data.model.MqttConfig
import com.example.sistemariego1.data.model.MqttConnectionState
import com.example.sistemariego1.ui.theme.CriticalRed
import com.example.sistemariego1.ui.theme.EmeraldGreenPrimary

@Composable
fun ConfigDialog(
    currentConfig: MqttConfig,
    connectionState: MqttConnectionState,
    onSaveAndConnect: (MqttConfig) -> Unit,
    onDisconnect: () -> Unit,
    onDismiss: () -> Unit
) {
    var ip by remember { mutableStateOf(currentConfig.brokerIp) }
    var portStr by remember { mutableStateOf(currentConfig.port.toString()) }
    var topicSensores by remember { mutableStateOf(currentConfig.topicSensores) }
    var topicBombaControl by remember { mutableStateOf(currentConfig.topicBombaControl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Configuración Servidor",
                    tint = EmeraldGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configuración Node-RED / MQTT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Insignia de Estado de Conexión
                ConnectionStatusBadge(connectionState = connectionState)

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Broker IP
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Servidor Node-RED / Broker") },
                    placeholder = { Text("Ej. 192.168.1.100") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Puerto
                OutlinedTextField(
                    value = portStr,
                    onValueChange = { portStr = it },
                    label = { Text("Puerto MQTT") },
                    placeholder = { Text("1883") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Tópico Sensores
                OutlinedTextField(
                    value = topicSensores,
                    onValueChange = { topicSensores = it },
                    label = { Text("Tópico de Sensores (Recepción JSON)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Tópico Control Bomba
                OutlinedTextField(
                    value = topicBombaControl,
                    onValueChange = { topicBombaControl = it },
                    label = { Text("Tópico Control Bomba (Envío JSON)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val port = portStr.toIntOrNull() ?: 1883
                    val newConfig = currentConfig.copy(
                        brokerIp = ip.trim(),
                        port = port,
                        topicSensores = topicSensores.trim(),
                        topicBombaControl = topicBombaControl.trim()
                    )
                    onSaveAndConnect(newConfig)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
            ) {
                Text("Guardar y Conectar")
            }
        },
        dismissButton = {
            Row {
                if (connectionState is MqttConnectionState.Connected) {
                    OutlinedButton(
                        onClick = {
                            onDisconnect()
                        }
                    ) {
                        Text("Desconectar", color = CriticalRed)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Composable
fun ConnectionStatusBadge(connectionState: MqttConnectionState) {
    val (bgColor, textColor, statusText, icon) = when (connectionState) {
        is MqttConnectionState.Connected -> Quadruple(
            Color(0xFFE8F5E9), Color(0xFF2E7D32), "CONECTADO", Icons.Default.CheckCircle
        )
        is MqttConnectionState.Connecting -> Quadruple(
            Color(0xFFFFF8E1), Color(0xFFF57F17), "CONECTANDO...", Icons.Default.Refresh
        )
        is MqttConnectionState.Disconnected -> Quadruple(
            Color(0xFFECEFF1), Color(0xFF607D8B), "DESCONECTADO", Icons.Default.WifiOff
        )
        is MqttConnectionState.Error -> Quadruple(
            Color(0xFFFFEBEE), CriticalRed, "ERROR: ${connectionState.message}", Icons.Default.Error
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
