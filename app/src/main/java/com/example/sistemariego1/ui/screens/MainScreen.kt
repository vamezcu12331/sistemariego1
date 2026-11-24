package com.example.sistemariego1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemariego1.data.model.MqttConnectionState
import com.example.sistemariego1.ui.components.ConfigDialog
import com.example.sistemariego1.ui.components.EmergencyPumpCard
import com.example.sistemariego1.ui.components.LightCard
import com.example.sistemariego1.ui.components.RainCard
import com.example.sistemariego1.ui.components.SoilMoistureCard
import com.example.sistemariego1.ui.components.TempHumidityCard
import com.example.sistemariego1.ui.components.WaterTankCard
import com.example.sistemariego1.ui.theme.EmeraldGreenPrimary
import com.example.sistemariego1.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val mqttConfig by viewModel.mqttConfig.collectAsState()

    var showConfigDialog by remember { mutableStateOf(false) }

    if (showConfigDialog) {
        ConfigDialog(
            currentConfig = mqttConfig,
            connectionState = connectionState,
            onSaveAndConnect = { newConfig ->
                viewModel.connectMqtt(newConfig)
            },
            onDisconnect = {
                viewModel.disconnectMqtt()
            },
            onDismiss = {
                showConfigDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "Sistema de Riego IoT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Broker: ${mqttConfig.brokerIp}:${mqttConfig.port}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    // Estado de Conexión abreviado en TopBar
                    ConnectionStateTopBadge(
                        connectionState = connectionState,
                        onClick = { showConfigDialog = true }
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(onClick = { viewModel.connectMqtt(mqttConfig) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reconectar",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurar Conexión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldGreenPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Secciones Principales
            Text(
                text = "PANEL DE CONTROL GENERAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de Control Manual de Emergencia de la Bomba
            EmergencyPumpCard(
                isPumpActive = sensorData.bombaEncendida,
                onTogglePump = { turnOn ->
                    viewModel.togglePump(turnOn)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MONITOREO DE SENSORES EN TIEMPO REAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Nivel del Tanque de Agua (Ancho completo)
            WaterTankCard(
                tankLevelPercent = sensorData.nivelAgua
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Humedad del Suelo
            SoilMoistureCard(
                moisturePercent = sensorData.humedadSuelo,
                soilState = sensorData.estadoSuelo
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Temperatura y Humedad Ambiental
            TempHumidityCard(
                temperature = sensorData.temperatura,
                humidity = sensorData.humedadAmbiente
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Sensor de Lluvia
            RainCard(
                isRaining = sensorData.lloviendo
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Intensidad de Luz
            LightCard(
                luxValue = sensorData.luzLux,
                lightState = sensorData.estadoLuz
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ConnectionStateTopBadge(
    connectionState: MqttConnectionState,
    onClick: () -> Unit
) {
    val (color, text) = when (connectionState) {
        is MqttConnectionState.Connected -> Color(0xFF4CAF50) to "Conectado"
        is MqttConnectionState.Connecting -> Color(0xFFFFB300) to "Conectando"
        is MqttConnectionState.Disconnected -> Color(0xFFE57373) to "Desconectado"
        is MqttConnectionState.Error -> Color(0xFFEF5350) to "Error"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = Color.White
        )
    }
}
