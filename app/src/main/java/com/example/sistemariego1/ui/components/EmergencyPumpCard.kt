package com.example.sistemariego1.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemariego1.ui.theme.CriticalRed
import com.example.sistemariego1.ui.theme.EmeraldGreenPrimary

@Composable
fun EmergencyPumpCard(
    isPumpActive: Boolean,
    onTogglePump: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBgColor by animateColorAsState(
        targetValue = if (isPumpActive) EmeraldGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        label = "cardBgColor"
    )

    val iconContainerColor by animateColorAsState(
        targetValue = if (isPumpActive) EmeraldGreenPrimary else Color.Gray.copy(alpha = 0.3f),
        label = "iconContainerColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Contenedor circular animado para el ícono de la bomba
                    Column(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(iconContainerColor),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isPumpActive) Icons.Default.WaterDrop else Icons.Default.PowerSettingsNew,
                            contentDescription = "Estado Bomba",
                            tint = if (isPumpActive) Color.White else Color.DarkGray,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "CONTROL DE BOMBA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (isPumpActive) "BOMBA ACTIVADA" else "BOMBA APAGADA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = if (isPumpActive) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Switch de corte de emergencia / activación manual
                Switch(
                    checked = isPumpActive,
                    onCheckedChange = { onTogglePump(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldGreenPrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtítulo informativo del payload JSON
            Text(
                text = if (isPumpActive) 
                    "● Riego en curso. Enviando payload {\"bomba\": \"ON\"}"
                else 
                    "○ Bomba detenida. Enviando payload {\"bomba\": \"OFF\"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (isPumpActive) EmeraldGreenPrimary else MaterialTheme.colorScheme.outline
            )
        }
    }
}
