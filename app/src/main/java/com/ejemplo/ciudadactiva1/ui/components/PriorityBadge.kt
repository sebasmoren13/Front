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
import com.ejemplo.ciudadactiva1.data.mock.Prioridad
import com.ejemplo.ciudadactiva1.ui.theme.*

@Composable
fun PriorityBadge(
    prioridad: Prioridad,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (prioridad) {
        Prioridad.ALTA -> PriorityHighContainer to PriorityHighText
        Prioridad.MEDIA -> PriorityMediumContainer to PriorityMediumText
        Prioridad.BAJA -> PriorityLowContainer to PriorityLowText
    }

    Box(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Prioridad ${prioridad.displayName}",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
