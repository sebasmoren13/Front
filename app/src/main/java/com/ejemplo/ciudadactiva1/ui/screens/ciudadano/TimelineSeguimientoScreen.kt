package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ejemplo.ciudadactiva1.data.mock.EstadoReporte
import com.ejemplo.ciudadactiva1.data.mock.EstadoTimelineStep
import com.ejemplo.ciudadactiva1.data.mock.MockRepository
import com.ejemplo.ciudadactiva1.data.mock.PasoLineaTiempo
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.PriorityBadge
import com.ejemplo.ciudadactiva1.ui.components.StatusBadge
import com.ejemplo.ciudadactiva1.ui.components.TimelineStepItem
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineSeguimientoScreen(
    reportId: String,
    reportesViewModel: ReportesCiudadanoViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToCierre: (String) -> Unit
) {
    val seguimientosState by reportesViewModel.seguimientosState.collectAsState()
    val allReports by MockRepository.reportesFlow.collectAsState()

    val report = remember(allReports, reportId) {
        allReports.find { it.id == reportId } ?: MockRepository.getReportes().first()
    }

    LaunchedEffect(reportId) {
        reportId.toLongOrNull()?.let { id ->
            reportesViewModel.cargarSeguimientos(id)
        }
    }

    val steps = remember(seguimientosState, report) {
        when (val state = seguimientosState) {
            is UiState.Success -> {
                if (state.data.isNotEmpty()) {
                    state.data.mapIndexed { index, seg ->
                        PasoLineaTiempo(
                            pasoNumero = index + 1,
                            titulo = seg.estadoNuevo ?: "Seguimiento #${index + 1}",
                            detalle = seg.observaciones ?: "Paso completado",
                            fecha = seg.fechaCambio ?: "Hoy",
                            estado = if (index == state.data.lastIndex) EstadoTimelineStep.ACTIVE else EstadoTimelineStep.COMPLETED,
                            responsable = "Sistema / Operador"
                        )
                    }
                } else report.timelineSteps
            }
            else -> report.timelineSteps
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento ${report.codigo}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (seguimientosState is UiState.Loading) {
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
                // Report Summary Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = report.categoria.displayName,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                fontSize = 12.sp
                            )
                            Row {
                                PriorityBadge(prioridad = report.prioridad)
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(estado = report.estado)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = report.titulo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = report.direccion,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        report.operadorAsignado?.let { op ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Asignado a: $op",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = ForestGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Línea de Tiempo del Servicio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                steps.forEachIndexed { index, step ->
                    TimelineStepItem(
                        step = step,
                        isLast = index == steps.lastIndex
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (report.estado == EstadoReporte.RESUELTO) {
                    Button(
                        onClick = { onNavigateToCierre(report.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Evidencia Final y Confirmar Cierre", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
