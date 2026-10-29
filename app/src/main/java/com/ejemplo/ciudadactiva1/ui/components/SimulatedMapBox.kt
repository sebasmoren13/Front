package com.ejemplo.ciudadactiva1.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.ciudadactiva1.data.mock.Categoria
import com.ejemplo.ciudadactiva1.data.mock.Reporte

@Composable
fun SimulatedMapBox(
    modifier: Modifier = Modifier,
    reports: List<Reporte> = emptyList(),
    selectedReportId: String? = null,
    onPinClick: ((Reporte) -> Unit)? = null,
    centerAddressText: String? = "Ubicación detectada por GPS (-12.045, -77.043)"
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F1F5))
            .border(1.dp, Color(0xFFBDC3C7), RoundedCornerShape(16.dp))
    ) {
        // Grid street simulation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Background streets
            val streetColor = Color(0xFFD5E1E8)
            val mainRoadColor = Color(0xFFF9E79F)

            // Horizontal roads
            drawLine(mainRoadColor, Offset(0f, height * 0.4f), Offset(width, height * 0.4f), strokeWidth = 24f)
            drawLine(streetColor, Offset(0f, height * 0.7f), Offset(width, height * 0.7f), strokeWidth = 14f)

            // Vertical roads
            drawLine(mainRoadColor, Offset(width * 0.5f, 0f), Offset(width * 0.5f, height), strokeWidth = 24f)
            drawLine(streetColor, Offset(width * 0.25f, 0f), Offset(width * 0.25f, height), strokeWidth = 12f)
            drawLine(streetColor, Offset(width * 0.75f, 0f), Offset(width * 0.75f, height), strokeWidth = 12f)
        }

        if (reports.isEmpty()) {
            // Single Centered Pin
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pin de ubicación",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = "GPS Activo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            }
        } else {
            // Multiple Pins mapped to coordinates/offsets
            val pinsOffsets = listOf(
                Offset(0.3f, 0.35f),
                Offset(0.6f, 0.45f),
                Offset(0.4f, 0.65f),
                Offset(0.7f, 0.25f),
                Offset(0.2f, 0.75f)
            )

            reports.forEachIndexed { index, report ->
                val offset = pinsOffsets[index % pinsOffsets.size]
                val isSelected = report.id == selectedReportId

                val pinColor = when (report.categoria) {
                    Categoria.VIAS -> Color(0xFFE74C3C)
                    Categoria.ALUMBRADO -> Color(0xFFF1C40F)
                    Categoria.RESIDUOS -> Color(0xFF2ECC71)
                    Categoria.PARQUES -> Color(0xFF9B59B6)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(
                                androidx.compose.ui.BiasAlignment(
                                    horizontalBias = (offset.x * 2) - 1f,
                                    verticalBias = (offset.y * 2) - 1f
                                )
                            )
                            .clickable { onPinClick?.invoke(report) }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 38.dp else 30.dp)
                                    .background(pinColor, CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = report.titulo,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = report.codigo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Address Bar Banner at top or bottom
        centerAddressText?.let { text ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
