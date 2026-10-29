package com.ejemplo.ciudadactiva1.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    adminViewModel: AdminViewModel = viewModel(),
    onBack: () -> Unit
) {
    val estadisticasState by adminViewModel.estadisticasState.collectAsState()

    LaunchedEffect(Unit) {
        adminViewModel.cargarEstadisticas()
    }

    val stats = remember(estadisticasState) {
        when (val state = estadisticasState) {
            is UiState.Success -> state.data
            else -> mapOf(
                "totalReportes" to 148,
                "porcentajeResueltos" to "89.2%",
                "tiempoPromedio" to "18.4 hrs",
                "viasPct" to 45f,
                "alumbradoPct" to 30f,
                "aseoPct" to 25f,
                "pendientesCount" to 8,
                "enAtencionCount" to 6,
                "resueltosCount" to 132,
                "rechazadosCount" to 2
            )
        }
    }

    val totalReportes = (stats["totalReportes"] ?: stats["total"] ?: 148).toString()
    val pctResueltos = (stats["porcentajeResueltos"] ?: stats["porcentajeResueltos"] ?: "89.2%").toString()
    val tiempoPromedio = (stats["tiempoPromedio"] ?: stats["tiempoPromedioHoras"] ?: "18.4 hrs").toString()

    val viasPct = ((stats["viasPct"] as? Number)?.toFloat() ?: 45f)
    val alumbradoPct = ((stats["alumbradoPct"] as? Number)?.toFloat() ?: 30f)
    val aseoPct = ((stats["aseoPct"] as? Number)?.toFloat() ?: 25f)

    val pendientesCount = ((stats["pendientesCount"] as? Number)?.toInt() ?: 8)
    val enAtencionCount = ((stats["enAtencionCount"] as? Number)?.toInt() ?: 6)
    val resueltosCount = ((stats["resueltosCount"] as? Number)?.toInt() ?: 132)
    val rechazadosCount = ((stats["rechazadosCount"] as? Number)?.toInt() ?: 2)

    val totalInt = totalReportes.toIntOrNull() ?: 148

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Indicadores y KPIs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (estadisticasState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ForestGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Tarjetas KPI Principales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tarjetas KPI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Total Reportes",
                        value = totalReportes,
                        color = Color(0xFF1B4F72),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "% Resueltos",
                        value = if (pctResueltos.endsWith("%")) pctResueltos else "$pctResueltos%",
                        color = ForestGreen,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Tiempo Prom.",
                        value = if (tiempoPromedio.contains("hr")) tiempoPromedio else "$tiempoPromedio hrs",
                        color = Color(0xFFD35400),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gráfico de Dona en Canvas para "Distribución por Categorías"
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Distribución por Categorías",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val viasSweep = (viasPct / 100f) * 360f
                            val alumbradoSweep = (alumbradoPct / 100f) * 360f
                            val aseoSweep = (aseoPct / 100f) * 360f

                            Box(
                                modifier = Modifier.size(130.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 32f
                                    drawArc(
                                        color = Color(0xFFE74C3C), // Vías
                                        startAngle = -90f,
                                        sweepAngle = viasSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawArc(
                                        color = Color(0xFFF1C40F), // Alumbrado
                                        startAngle = -90f + viasSweep,
                                        sweepAngle = alumbradoSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawArc(
                                        color = Color(0xFF2ECC71), // Aseo
                                        startAngle = -90f + viasSweep + alumbradoSweep,
                                        sweepAngle = aseoSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("100%", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Total", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DonutLegendItem(color = Color(0xFFE74C3C), label = "Vías", value = "${viasPct.toInt()}%")
                                DonutLegendItem(color = Color(0xFFF1C40F), label = "Alumbrado", value = "${alumbradoPct.toInt()}%")
                                DonutLegendItem(color = Color(0xFF2ECC71), label = "Aseo / Residuos", value = "${aseoPct.toInt()}%")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Barras de progreso de "Reportes por Estado"
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Reportes por Estado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProgressBarItem(
                            label = "Pendientes ($pendientesCount)",
                            progress = if (totalInt > 0) pendientesCount.toFloat() / totalInt else 0f,
                            color = Color(0xFFE67E22),
                            countText = "$pendientesCount"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProgressBarItem(
                            label = "En Atención / Proceso ($enAtencionCount)",
                            progress = if (totalInt > 0) enAtencionCount.toFloat() / totalInt else 0f,
                            color = Color(0xFF2980B9),
                            countText = "$enAtencionCount"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProgressBarItem(
                            label = "Resueltos ($resueltosCount)",
                            progress = if (totalInt > 0) resueltosCount.toFloat() / totalInt else 0f,
                            color = ForestGreen,
                            countText = "$resueltosCount"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProgressBarItem(
                            label = "Rechazados ($rechazadosCount)",
                            progress = if (totalInt > 0) rechazadosCount.toFloat() / totalInt else 0f,
                            color = Color(0xFFC0392B),
                            countText = "$rechazadosCount"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DonutLegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ProgressBarItem(
    label: String,
    progress: Float,
    color: Color,
    countText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = countText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
