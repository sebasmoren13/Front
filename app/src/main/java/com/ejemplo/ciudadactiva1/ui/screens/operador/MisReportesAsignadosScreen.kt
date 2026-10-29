package com.ejemplo.ciudadactiva1.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.PowerSettingsNew
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
fun MisReportesAsignadosScreen(
    operadorViewModel: OperadorViewModel = viewModel(),
    onNavigateToDetalle: (String) -> Unit,
    onLogout: () -> Unit
) {
    val asignadosState by operadorViewModel.misReportesAsignadosState.collectAsState()

    LaunchedEffect(Unit) {
        operadorViewModel.cargarReportesAsignados()
    }

    val assignedReports = remember(asignadosState) {
        when (val state = asignadosState) {
            is UiState.Success -> {
                state.data.map { dto ->
                    Reporte(
                        id = dto.id?.toString() ?: "102",
                        codigo = "#CA-${dto.id ?: 102}",
                        titulo = dto.titulo ?: "Orden de Trabajo",
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
                        estado = EstadoReporte.EN_PROCESO,
                        direccion = dto.ubicacion ?: "Calle Las Flores 120",
                        latitud = -12.049,
                        longitud = -77.039,
                        fechaRegistro = dto.fechaCreacion ?: "Ayer",
                        tiempoTranscurrido = "En campo",
                        fotoUrl = dto.imagen ?: "https://picsum.photos/400/300?random=${dto.id ?: 102}"
                    )
                }
            }
            else -> emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reportes Asignados - Operador", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Cerrar sesión",
                            tint = Color.Red
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
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B4F72))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Engineering,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Cuadrilla Operación en Campo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${assignedReports.size} reportes pendientes de atención",
                            color = Color(0xFFA9CCE3),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            when (val state = asignadosState) {
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
                    if (assignedReports.isEmpty()) {
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
                            items(assignedReports) { report ->
                                OperadorCardItem(
                                    reporte = report,
                                    onVerDetalleClick = { onNavigateToDetalle(report.id) }
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
fun OperadorCardItem(
    reporte: Reporte,
    onVerDetalleClick: () -> Unit
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
                text = "📍 ${reporte.direccion}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            PriorityBadge(prioridad = reporte.prioridad)

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onVerDetalleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Ver Detalle del Reporte", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
