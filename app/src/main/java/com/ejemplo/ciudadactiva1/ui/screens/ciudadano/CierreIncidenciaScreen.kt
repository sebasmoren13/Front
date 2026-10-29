package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ejemplo.ciudadactiva1.data.mock.MockRepository
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.StarRatingBar
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreIncidenciaScreen(
    reportId: String,
    cierreViewModel: CierreIncidenciaViewModel = viewModel(),
    onBack: () -> Unit,
    onFinalizado: () -> Unit
) {
    val cierreState by cierreViewModel.cierreState.collectAsState()

    val allReports by MockRepository.reportesFlow.collectAsState()
    val report = remember(allReports, reportId) {
        allReports.find { it.id == reportId } ?: MockRepository.getReportes().first()
    }

    var starRating by remember { mutableIntStateOf(report.valoracionEstrellas ?: 5) }
    var comentariosFinales by remember { mutableStateOf(report.comentariosCierreCiudadano ?: "") }
    var showActionMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Cierre de Incidencia", fontWeight = FontWeight.Bold) },
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
            Text(
                text = "${report.codigo} - ${report.titulo}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta Evidencia final comparativa con AsyncImage
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Evidencia Final Comparativa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Foto "Antes"
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = report.fotoUrl,
                                contentDescription = "Antes",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFADBD8))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Antes (Reportado)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }

                        // Foto "Después"
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = report.fotoDespuesUrl ?: "https://picsum.photos/400/300?random=14",
                                contentDescription = "Después",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFD4EFDF))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Después (Solucionado)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Caja con texto "Cierre del operador"
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5FB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Observaciones del Cierre (Operador):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1B4F72)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.observacionesCierreOperador
                            ?: report.observacionesAvance
                            ?: "Trabajo de campo concluido en el horario programado. Zona rehabilitada.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Control interactivo de 5 Estrellas
            Text(
                text = "Nivel de satisfacción *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                StarRatingBar(
                    rating = starRating,
                    onRatingChanged = { starRating = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para comentarios finales
            Text(
                text = "Comentarios finales",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = comentariosFinales,
                onValueChange = { comentariosFinales = it },
                placeholder = { Text("Escriba su opinión sobre el servicio recibido...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp)
            )

            showActionMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Objetar / Reabrir Caso
                OutlinedButton(
                    onClick = {
                        cierreViewModel.objetarReabrirCaso(
                            reporteId = report.id.toLongOrNull() ?: 104L,
                            comentarios = comentariosFinales.ifBlank { "Cliente objetó el trabajo." },
                            onSuccess = {
                                showActionMessage = "El caso ha sido reabierto para revisión."
                                onFinalizado()
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC0392B)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFC0392B))),
                    enabled = cierreState !is UiState.Loading
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Objetar / Reabrir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Confirmar y Finalizar
                Button(
                    onClick = {
                        cierreViewModel.cerrarReporte(
                            reporteId = report.id.toLongOrNull() ?: 104L,
                            comentarios = comentariosFinales,
                            estrellas = starRating,
                            onSuccess = {
                                showActionMessage = "¡Gracias! Su confirmación ha sido guardada."
                                onFinalizado()
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    enabled = cierreState !is UiState.Loading
                ) {
                    if (cierreState is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar Cierre", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
