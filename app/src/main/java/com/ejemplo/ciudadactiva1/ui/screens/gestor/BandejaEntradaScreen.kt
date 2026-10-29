package com.ejemplo.ciudadactiva1.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
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
import com.ejemplo.ciudadactiva1.ui.components.RejectionDialog
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaEntradaScreen(
    gestorViewModel: GestorViewModel = viewModel(),
    onNavigateToAdmitir: (String) -> Unit,
    onLogout: () -> Unit
) {
    val pendientesState by gestorViewModel.pendientesState.collectAsState()

    var reportToReject by remember { mutableStateOf<Reporte?>(null) }

    LaunchedEffect(Unit) {
        gestorViewModel.cargarReportesPendientes()
    }

    val pendingReports = remember(pendientesState) {
        when (val state = pendientesState) {
            is UiState.Success -> {
                state.data.map { dto ->
                    Reporte(
                        id = dto.id?.toString() ?: "101",
                        codigo = "#CA-${dto.id ?: 101}",
                        titulo = dto.titulo ?: "Incidente Pendiente",
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
                        estado = EstadoReporte.PENDIENTE,
                        direccion = dto.ubicacion ?: "Av. Central 452",
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandeja de Entrada - Gestor", fontWeight = FontWeight.Bold) },
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
            // Header Stats Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${pendingReports.size} Reportes Pendientes por Evaluar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Requieren validación y asignación de cuadrilla",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            when (val state = pendientesState) {
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
                    if (pendingReports.isEmpty()) {
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
                            items(pendingReports) { report ->
                                PendingReportCard(
                                    reporte = report,
                                    onAdmitirClick = { onNavigateToAdmitir(report.id) },
                                    onRechazarClick = { reportToReject = report }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Emergente de Rechazo
    reportToReject?.let { report ->
        RejectionDialog(
            onDismissRequest = { reportToReject = null },
            onConfirmRejection = { justificacion ->
                gestorViewModel.rechazarReporte(
                    reporteId = report.id.toLongOrNull() ?: 101L,
                    justificacion = justificacion,
                    onSuccess = { reportToReject = null }
                )
            }
        )
    }
}

@Composable
fun PendingReportCard(
    reporte: Reporte,
    onAdmitirClick: () -> Unit,
    onRechazarClick: () -> Unit
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
                Text(
                    text = reporte.fechaRegistro,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reporte.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = reporte.descripcion,
                fontSize = 13.sp,
                color = Color.DarkGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "📍 ${reporte.direccion}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            PriorityBadge(prioridad = reporte.prioridad)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRechazarClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFADBD8),
                        contentColor = Color(0xFF78281F)
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rechazar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onAdmitirClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Admitir", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
