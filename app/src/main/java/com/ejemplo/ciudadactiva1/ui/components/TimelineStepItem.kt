package com.ejemplo.ciudadactiva1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.ciudadactiva1.data.mock.EstadoTimelineStep
import com.ejemplo.ciudadactiva1.data.mock.PasoLineaTiempo

@Composable
fun TimelineStepItem(
    step: PasoLineaTiempo,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Timeline Indicator Column (Icon/Circle + Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            when (step.estado) {
                EstadoTimelineStep.COMPLETED -> {
                    // Green Check
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF27AE60), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completado",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                EstadoTimelineStep.ACTIVE -> {
                    // Active Blue Circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF2980B9), CircleShape)
                            .border(3.dp, Color(0xFFA9CCE3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.pasoNumero.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                EstadoTimelineStep.PENDING -> {
                    // Pending Gray Circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFBDC3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.pasoNumero.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(50.dp)
                        .background(
                            if (step.estado == EstadoTimelineStep.COMPLETED) Color(0xFF27AE60) else Color(0xFFD5D8DC)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step Info Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (step.estado == EstadoTimelineStep.ACTIVE) Color(0xFFEBF5FB)
                    else Color.White
                )
                .border(
                    width = 1.dp,
                    color = if (step.estado == EstadoTimelineStep.ACTIVE) Color(0xFF3498DB) else Color(0xFFEAEDED),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${step.pasoNumero}. ${step.titulo}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (step.estado == EstadoTimelineStep.PENDING) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = step.fecha,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.detalle,
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            step.responsable?.let { resp ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Responsable: $resp",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
