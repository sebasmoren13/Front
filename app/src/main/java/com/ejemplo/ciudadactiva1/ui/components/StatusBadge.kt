package com.ejemplo.ciudadactiva1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.ciudadactiva1.data.mock.EstadoReporte
import com.ejemplo.ciudadactiva1.ui.theme.*

@Composable
fun StatusBadge(
    estado: EstadoReporte,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (estado) {
        EstadoReporte.PENDIENTE -> StatusPendingContainer to StatusPendingText
        EstadoReporte.EN_PROCESO -> StatusInProgressContainer to StatusInProgressText
        EstadoReporte.RESUELTO -> StatusResolvedContainer to StatusResolvedText
        EstadoReporte.RECHAZADO -> StatusRejectedContainer to StatusRejectedText
    }

    Box(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.displayName,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
