package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ejemplo.ciudadactiva1.data.mock.Categoria
import com.ejemplo.ciudadactiva1.data.mock.EstadoReporte
import com.ejemplo.ciudadactiva1.data.mock.Prioridad
import com.ejemplo.ciudadactiva1.data.mock.Reporte
import com.ejemplo.ciudadactiva1.data.network.model.Reporte as NetworkReporte
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.PriorityBadge
import com.ejemplo.ciudadactiva1.ui.components.SimulatedMapBox
import com.ejemplo.ciudadactiva1.ui.components.StatusBadge
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerIncidentesScreen(
    reportesViewModel: ReportesCiudadanoViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToTimeline: (String) -> Unit
) {
    var isMapView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Categoria?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<EstadoReporte?>(null) }
    var selectedMapReport by remember { mutableStateOf<Reporte?>(null) }

    val reportesState by reportesViewModel.reportesState.collectAsState()

    LaunchedEffect(Unit) {
        reportesViewModel.cargarReportes()
    }

    val displayReports = remember(reportesState) {
        when (val state = reportesState) {
            is UiState.Success -> state.data.map { dto -> mapNetworkReporteToDisplay(dto) }
            else -> emptyList()
        }
    }

    val filteredReports = remember(displayReports, searchQuery, selectedCategoryFilter, selectedStatusFilter) {
        displayReports.filter { report ->
            val matchesQuery = searchQuery.isBlank() ||
                    report.direccion.contains(searchQuery, ignoreCase = true) ||
                    report.codigo.contains(searchQuery, ignoreCase = true) ||
                    report.titulo.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategoryFilter == null || report.categoria == selectedCategoryFilter
            val matchesStatus = selectedStatusFilter == null || report.estado == selectedStatusFilter

            matchesQuery && matchesCategory && matchesStatus
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidentes en la Ciudad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isMapView) Icons.Default.Map else Icons.Default.List,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMapView) "Mapa" else "Lista",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isMapView,
                            onCheckedChange = { isMapView = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ForestGreen,
                                checkedTrackColor = ForestGreen.copy(alpha = 0.3f)
                            )
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
                placeholder = { Text("Buscar por calle o código (#CA-101)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Horizontal Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null && selectedStatusFilter == null,
                        onClick = {
                            selectedCategoryFilter = null
                            selectedStatusFilter = null
                        },
                        label = { Text("Todos") }
                    )
                }

                items(Categoria.entries) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                        },
                        label = { Text(cat.displayName) }
                    )
                }

                items(EstadoReporte.entries) { st ->
                    FilterChip(
                        selected = selectedStatusFilter == st,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == st) null else st
                        },
                        label = { Text(st.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = reportesState) {
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
                    } else if (isMapView) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SimulatedMapBox(
                                modifier = Modifier.fillMaxSize(),
                                reports = filteredReports,
                                selectedReportId = selectedMapReport?.id,
                                onPinClick = { report -> selectedMapReport = report },
                                centerAddressText = "Toque un pin para ver la tarjeta del reporte"
                            )

                            selectedMapReport?.let { report ->
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clickable { onNavigateToTimeline(report.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = report.codigo,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreen,
                                                fontSize = 14.sp
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
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            text = report.direccion,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Ver detalle e historial ➔",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = ForestGreen
                                            )
                                            Text(
                                                text = report.fechaRegistro,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredReports) { report ->
                                IncidenteCardItem(
                                    reporte = report,
                                    onClick = { onNavigateToTimeline(report.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun mapNetworkReporteToDisplay(dto: NetworkReporte): Reporte {
    return Reporte(
        id = dto.id?.toString() ?: "101",
        codigo = "#CA-${dto.id ?: 101}",
        titulo = dto.titulo ?: "Incidente Urbano",
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

@Composable
fun IncidenteCardItem(
    reporte: Reporte,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = reporte.fotoUrl,
                contentDescription = reporte.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD5D8DC))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reporte.codigo,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        fontSize = 12.sp
                    )
                    Text(
                        text = reporte.fechaRegistro,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                Text(
                    text = reporte.direccion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityBadge(prioridad = reporte.prioridad)
                    StatusBadge(estado = reporte.estado)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detalle",
                tint = Color.Gray
            )
        }
    }
}
