package com.ejemplo.ciudadactiva1.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MonitoreoReportesActivosScreen(
    adminViewModel: AdminViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToTimeline: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var sortBySla by remember { mutableStateOf(false) }

    val filterOptions = listOf("Todos los Activos", "En Atención", "Pendientes de Asignación", "Prioridad Alta")

    val reportesActivosState by adminViewModel.reportesActivosState.collectAsState()

    LaunchedEffect(Unit) {
        adminViewModel.cargarReportesActivos()
    }

    val displayReports = remember(reportesActivosState) {
        when (val state = reportesActivosState) {
            is UiState.Success -> {
                state.data.map { dto ->
                    Reporte(
                        id = dto.id?.toString() ?: "101",
                        codigo = "#CA-${dto.id ?: 101}",
                        titulo = dto.titulo ?: "Reporte Activo",
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
                        direccion = dto.ubicacion ?: "Av. Principal 123",
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

    val filteredReports = remember(displayReports, searchQuery, selectedFilterIndex, sortBySla) {
        var result = displayReports.filter { report ->
            val matchesSearch = searchQuery.isBlank() ||
                    report.codigo.contains(searchQuery, ignoreCase = true) ||
                    report.direccion.contains(searchQuery, ignoreCase = true) ||
                    report.titulo.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilterIndex) {
                1 -> report.estado == EstadoReporte.EN_PROCESO
                2 -> report.estado == EstadoReporte.PENDIENTE
                3 -> report.prioridad == Prioridad.ALTA
                else -> report.estado != EstadoReporte.RECHAZADO
            }

            matchesSearch && matchesFilter
        }

        if (sortBySla) {
            result = result.sortedByDescending { it.tiempoTranscurrido }
        }

        result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo de Reportes Activos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { sortBySla = !sortBySla }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Ordenar por SLA",
                            tint = if (sortBySla) ForestGreen else Color.Gray
                        )
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
            // SearchBar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por código (#CA-104), dirección o título...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions.size) { index ->
                    FilterChip(
                        selected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index },
                        label = { Text(filterOptions[index]) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = reportesActivosState) {
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredReports) { report ->
                                AdminReportMonitoringItem(
                                    reporte = report,
                                    onVerDetalleClick = { onNavigateToTimeline(report.id) }
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
fun AdminReportMonitoringItem(
    reporte: Reporte,
    onVerDetalleClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onVerDetalleClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reporte.codigo,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    fontSize = 14.sp
                )
                Text(
                    text = "⏱️ SLA: ${reporte.tiempoTranscurrido}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD35400)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reporte.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = "📍 ${reporte.direccion}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PriorityBadge(prioridad = reporte.prioridad)
                    StatusBadge(estado = reporte.estado)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ver detalle ➔",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
