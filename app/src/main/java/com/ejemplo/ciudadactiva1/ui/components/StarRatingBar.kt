package com.ejemplo.ciudadactiva1.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Row(modifier = modifier) {
        (1..5).forEach { starIndex ->
            Icon(
                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Estrella $starIndex",
                tint = if (starIndex <= rating) Color(0xFFF1C40F) else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp)
                    .clickable(enabled = !readOnly) {
                        onRatingChanged(starIndex)
                    }
            )
        }
    }
}
