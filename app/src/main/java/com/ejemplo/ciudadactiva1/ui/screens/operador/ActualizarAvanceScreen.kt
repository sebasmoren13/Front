package com.ejemplo.ciudadactiva1.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ejemplo.ciudadactiva1.data.mock.MockRepository
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.DottedCameraBox
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarAvanceScreen(
    reportId: String,
    operadorViewModel: OperadorViewModel = viewModel(),
    onBack: () -> Unit,
    onGuardadoExitoso: () -> Unit
) {
    val avanceState by operadorViewModel.avanceState.collectAsState()

    val estadoTransiciones = listOf(
        "En Inspección Técnica",
        "En Reparación y Mantenimiento",
        "Materiales Aplicados",
        "Atención Finalizada en Campo"
    )

    var selectedTransicion by remember { mutableStateOf(estadoTransiciones.first()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var observacionesText by remember { mutableStateOf("") }
    var isPhotoAttached by remember { mutableStateOf(false) }

    val allReports by MockRepository.reportesFlow.collectAsState()
    val report = remember(allReports, reportId) {
        allReports.find { it.id == reportId } ?: MockRepository.getReportes().first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualizar Avance ${report.codigo}", fontWeight = FontWeight.Bold) },
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
                text = "Registro de Avance Técnico",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreen
            )

            Text(
                text = "Reporte: ${report.titulo}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dropdown Transición de Estado
            Text(
                text = "Transición de Estado *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedTransicion,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    estadoTransiciones.forEach { trans ->
                        DropdownMenuItem(
                            text = { Text(trans) },
                            onClick = {
                                selectedTransicion = trans
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Observaciones del avance *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = observacionesText,
                onValueChange = { observacionesText = it },
                placeholder = { Text("Detalle las acciones realizadas en campo, materiales utilizados o novedades...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fotografía de Progreso",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            DottedCameraBox(
                textLabel = "Adjuntar Fotografía de avance de obra",
                isPhotoAttached = isPhotoAttached,
                onClick = { isPhotoAttached = !isPhotoAttached }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    operadorViewModel.registrarAvance(
                        reporteId = report.id.toLongOrNull() ?: 102L,
                        nuevoEstado = selectedTransicion,
                        observaciones = observacionesText,
                        evidenciaUrl = if (isPhotoAttached) "https://picsum.photos/400/300?random=55" else null,
                        onSuccess = onGuardadoExitoso
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                enabled = observacionesText.isNotBlank() && avanceState !is UiState.Loading
            ) {
                if (avanceState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Avance y Notificar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
