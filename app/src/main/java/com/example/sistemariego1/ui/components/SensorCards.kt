package com.example.sistemariego1.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemariego1.data.model.LightState
import com.example.sistemariego1.data.model.SoilState
import com.example.sistemariego1.ui.theme.CriticalRed
import com.example.sistemariego1.ui.theme.LightCloudyColor
import com.example.sistemariego1.ui.theme.LightDayColor
import com.example.sistemariego1.ui.theme.LightNightColor
import com.example.sistemariego1.ui.theme.RainAlertColor
import com.example.sistemariego1.ui.theme.RainClearColor
import com.example.sistemariego1.ui.theme.SoilDryColor
import com.example.sistemariego1.ui.theme.SoilOptimalColor
import com.example.sistemariego1.ui.theme.SoilWetColor
import com.example.sistemariego1.ui.theme.WaterNormalBlue

// 1. Tarjeta: Humedad del Suelo
@Composable
fun SoilMoistureCard(
    moisturePercent: Float,
    soilState: SoilState,
    modifier: Modifier = Modifier
) {
    val stateColor = when (soilState) {
        SoilState.SECO -> SoilDryColor
        SoilState.OPTIMO -> SoilOptimalColor
        SoilState.HUMEDO -> SoilWetColor
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (moisturePercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "moistureProgress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Humedad del Suelo",
                        tint = stateColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Humedad del Suelo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Chip con el Estado (Seco, Óptimo, Húmedo)
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = soilState.label,
                            fontWeight = FontWeight.Bold,
                            color = stateColor
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = stateColor.copy(alpha = 0.15f)
                    ),
                    border = null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indicador Medidor Circular
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        // Fondo del arco
                        drawArc(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Arco del valor actual
                        drawArc(
                            color = stateColor,
                            startAngle = 135f,
                            sweepAngle = 270f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Text(
                        text = "${moisturePercent.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = stateColor
                    )
                }
            }
        }
    }
}

// 2. Tarjeta: Temperatura y Humedad Ambiental (DHT11/DHT22)
@Composable
fun TempHumidityCard(
    temperature: Float,
    humidity: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ambiente (DHT11 / DHT22)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Bloque Temperatura
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF7043).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "Temperatura",
                            tint = Color(0xFFFF7043),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Temperatura",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "%.1f °C".format(temperature),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }

                // Bloque Humedad Relativa
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(WaterNormalBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = "Humedad Relativa",
                            tint = WaterNormalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Humedad Rel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "%.1f %%".format(humidity),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }
            }
        }
    }
}

// 3. Tarjeta: Sensor de Lluvia
@Composable
fun RainCard(
    isRaining: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isRaining) RainAlertColor.copy(alpha = 0.12f) else RainClearColor.copy(alpha = 0.08f)
    val statusColor = if (isRaining) RainAlertColor else RainClearColor

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRaining) Icons.Default.Thunderstorm else Icons.Default.WbSunny,
                        contentDescription = "Lluvia",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Sensor de Lluvia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (isRaining) "¡ESTÁ LLOVIENDO!" else "Despejado / Sin lluvia",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            if (isRaining) {
                Text(
                    text = "🌧️ Alerta Lluvia",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RainAlertColor,
                    modifier = Modifier
                        .background(RainAlertColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// 4. Tarjeta: Intensidad de Luz (LDR / Lux)
@Composable
fun LightCard(
    luxValue: Float,
    lightState: LightState,
    modifier: Modifier = Modifier
) {
    val stateColor = when (lightState) {
        LightState.DIA -> LightDayColor
        LightState.NUBLADO -> LightCloudyColor
        LightState.NOCHE -> LightNightColor
    }

    val stateIcon = when (lightState) {
        LightState.DIA -> Icons.Default.LightMode
        LightState.NUBLADO -> Icons.Default.Cloud
        LightState.NOCHE -> Icons.Default.NightsStay
    }

    val progressValue = (luxValue / 1000f).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = stateIcon,
                        contentDescription = "Intensidad de Luz",
                        tint = stateColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Intensidad de Luz",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = "${luxValue.toInt()} Lux (${lightState.label})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = stateColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = stateColor,
                trackColor = stateColor.copy(alpha = 0.2f)
            )
        }
    }
}

// 5. Tarjeta: Nivel de Agua del Tanque (Representación visual animada con alerta roja < 20%)
@Composable
fun WaterTankCard(
    tankLevelPercent: Float,
    modifier: Modifier = Modifier
) {
    val isCritical = tankLevelPercent < 20f
    val waterColor = if (isCritical) CriticalRed else WaterNormalBlue

    val animatedFillProgress by animateFloatAsState(
        targetValue = (tankLevelPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "tankLevelAnimation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical) CriticalRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nivel de Agua (Tanque)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "${tankLevelPercent.toInt()}%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = waterColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dibujo Composable del Tanque de Agua
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Representación de tanque vertical
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(110.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, waterColor, RoundedCornerShape(10.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Contenido de Agua
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedFillProgress)
                            .background(waterColor)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Mensajes explicativos y Alerta Crítica
                Column(modifier = Modifier.weight(1f)) {
                    if (isCritical) {
                        Text(
                            text = "⚠️ ¡NIVEL CRÍTICO DE AGUA!",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CriticalRed
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "El tanque tiene menos del 20% de agua disponible. Por favor reabastezca.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CriticalRed
                        )
                    } else {
                        Text(
                            text = "Nivel de Agua Adecuado",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = WaterNormalBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Capacidad del depósito suficiente para ciclos de riego automáticos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
