package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ejemplo.ciudadactiva1.data.mock.Categoria
import com.ejemplo.ciudadactiva1.data.mock.EstadoReporte
import com.ejemplo.ciudadactiva1.data.mock.Prioridad
import com.ejemplo.ciudadactiva1.data.mock.Reporte
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.PriorityBadge
import com.ejemplo.ciudadactiva1.ui.components.StatusBadge
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialRegistrosScreen(
    reportesViewModel: ReportesCiudadanoViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToTimeline: (String) -> Unit,
    onNavigateToCierre: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Todos", "En Proceso", "Resueltos", "Rechazados")

    val misReportesState by reportesViewModel.misReportesState.collectAsState()

    LaunchedEffect(Unit) {
        reportesViewModel.cargarMisReportes()
    }

    val userReports = remember(misReportesState) {
        when (val state = misReportesState) {
            is UiState.Success -> {
                state.data.map { dto ->
                    Reporte(
                        id = dto.id?.toString() ?: "101",
                        codigo = "#CA-${dto.id ?: 101}",
                        titulo = dto.titulo ?: "Reporte Registrado",
                        descripcion = dto.descripcion ?: "Sin descripción",
                        categoria = when (dto.categoriaIncidencia?.id) {
                            1L -> Categoria.VIAS
                            2L -> Categoria.ALUMBRADO
                            3L -> Categoria.RESIDUOS
                            else -> Categoria.PARQUES
                        },
                        prioridad = when (dto.prioridad?.uppercase()) {
                            "ALTA" -> Prioridad.ALTA
                            "BAJA" -> Prioridad.BAJA
                            else -> Prioridad.MEDIA
                        },
                        estado = when (dto.estado?.uppercase()) {
                            "PENDIENTE" -> EstadoReporte.PENDIENTE
                            "EN_PROCESO" -> EstadoReporte.EN_PROCESO
                            "RESUELTO" -> EstadoReporte.RESUELTO
                            "RECHAZADO" -> EstadoReporte.RECHAZADO
                            else -> EstadoReporte.PENDIENTE
                        },
                        direccion = dto.ubicacion ?: "Dirección registrada",
                        latitud = -12.045,
                        longitud = -77.043,
                        fechaRegistro = dto.fechaCreacion ?: "Hoy",
                        tiempoTranscurrido = "Reciente",
                        fotoUrl = dto.imagen ?: "https://picsum.photos/400/300?random=${dto.id ?: 101}"
                    )
                }
            }
            else -> emptyList()
        }
    }

    val filteredReports = remember(userReports, selectedTabIndex) {
        when (selectedTabIndex) {
            1 -> userReports.filter { it.estado == EstadoReporte.EN_PROCESO || it.estado == EstadoReporte.PENDIENTE }
            2 -> userReports.filter { it.estado == EstadoReporte.RESUELTO }
            3 -> userReports.filter { it.estado == EstadoReporte.RECHAZADO }
            else -> userReports
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial de Registros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ForestGreen
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = misReportesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ForestGreen)
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error de servidor: ${state.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    if (filteredReports.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay reportes registrados en la base de datos",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredReports) { report ->
                                HistorialCardItem(
                                    reporte = report,
                                    onVerSeguimientoClick = { onNavigateToTimeline(report.id) },
                                    onVerCierreClick = { onNavigateToCierre(report.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialCardItem(
    reporte: Reporte,
    onVerSeguimientoClick: () -> Unit,
    onVerCierreClick: () -> Unit
) {
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
                    text = "${reporte.codigo} • ${reporte.categoria.displayName}",
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    fontSize = 13.sp
                )
                StatusBadge(estado = reporte.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reporte.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = reporte.direccion,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            PriorityBadge(prioridad = reporte.prioridad)

            if (reporte.estado == EstadoReporte.RECHAZADO) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFADBD8), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Justificación",
                            tint = Color(0xFF900C3F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Justificación:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78281F),
                                fontSize = 12.sp
                            )
                            Text(
                                text = reporte.justificacionRechazo ?: "No se especificó razón.",
                                color = Color(0xFF78281F),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (reporte.estado == EstadoReporte.RESUELTO) {
                    OutlinedButton(
                        onClick = onVerCierreClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ver Evidencia / Calificar", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onVerSeguimientoClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver Seguimiento", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
