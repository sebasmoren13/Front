package com.ejemplo.ciudadactiva1.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ejemplo.ciudadactiva1.data.mock.MockRepository
import com.ejemplo.ciudadactiva1.ui.components.PriorityBadge
import com.ejemplo.ciudadactiva1.ui.components.SimulatedMapBox
import com.ejemplo.ciudadactiva1.ui.components.StatusBadge
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReporteAsignadoScreen(
    reportId: String,
    operadorViewModel: OperadorViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToActualizarAvance: (String) -> Unit,
    onFinalizadoOperador: () -> Unit
) {
    val allReports by MockRepository.reportesFlow.collectAsState()
    val report = remember(allReports, reportId) {
        allReports.find { it.id == reportId } ?: MockRepository.getReportes().first()
    }

    var showFinalizarDialog by remember { mutableStateOf(false) }
    var observacionesFinalesText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden de Trabajo ${report.codigo}", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Summary Card
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
                            fontSize = 13.sp
                        )
                        Row {
                            PriorityBadge(prioridad = report.prioridad)
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(estado = report.estado)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Descripción del ciudadano:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = report.descripcion,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información técnica e Imagen Inicial con Coil AsyncImage
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMACIÓN TÉCNICA Y UBICACIÓN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ForestGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "📍 Dirección: ${report.direccion}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "⏱️ Tiempo transcurrido: ${report.tiempoTranscurrido}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = report.fotoUrl,
                        contentDescription = "Imagen Inicial",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE5E8E8))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SimulatedMapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        centerAddressText = "Ubicación en mapa GPS"
                    )
                }
            }

            report.observacionesAvance?.let { obs ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8F5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Último Avance Registrado:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = obs, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onNavigateToActualizarAvance(report.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980B9))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar Avance de Cuadrilla", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = { showFinalizarDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar y Finalizar Reporte", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // Dialog para Cerrar y Finalizar Reporte por el operador
    if (showFinalizarDialog) {
        AlertDialog(
            onDismissRequest = { showFinalizarDialog = false },
            title = { Text("Finalizar Trabajo de Campo", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ingrese observaciones finales del trabajo realizado:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = observacionesFinalesText,
                        onValueChange = { observacionesFinalesText = it },
                        placeholder = { Text("Ej: Trabajo finalizado. Pavimento compactado y seco.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        operadorViewModel.finalizarReporte(
                            reporteId = report.id.toLongOrNull() ?: 102L,
                            observacionesFinales = observacionesFinalesText.ifBlank { "Atención en campo concluida con éxito." },
                            onSuccess = {
                                showFinalizarDialog = false
                                onFinalizadoOperador()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Confirmar Cierre")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalizarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
