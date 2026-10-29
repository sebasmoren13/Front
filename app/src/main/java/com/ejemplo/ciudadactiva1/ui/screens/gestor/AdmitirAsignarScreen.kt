package com.ejemplo.ciudadactiva1.ui.screens.gestor

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
import com.ejemplo.ciudadactiva1.data.mock.MockRepository
import com.ejemplo.ciudadactiva1.data.mock.Prioridad
import com.ejemplo.ciudadactiva1.data.network.model.Usuario
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.SimulatedMapBox
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmitirAsignarScreen(
    reportId: String,
    gestorViewModel: GestorViewModel = viewModel(),
    onBack: () -> Unit,
    onAdmitidoExitoso: () -> Unit
) {
    val allReports by MockRepository.reportesFlow.collectAsState()
    val report = remember(allReports, reportId) {
        allReports.find { it.id == reportId } ?: MockRepository.getReportes().first()
    }

    val operadoresState by gestorViewModel.operadoresState.collectAsState()
    val accionState by gestorViewModel.accionState.collectAsState()

    var selectedPriority by remember { mutableStateOf(report.prioridad) }
    var expandedOperadorDropdown by remember { mutableStateOf(false) }

    val operadoresList = remember(operadoresState) {
        when (val state = operadoresState) {
            is UiState.Success -> state.data
            else -> listOf(
                Usuario(id = 3L, nombre = "Cuadrilla 1", apellido = "Vías y Pavimento"),
                Usuario(id = 4L, nombre = "Cuadrilla 2", apellido = "Alumbrado Público"),
                Usuario(id = 5L, nombre = "Cuadrilla 3", apellido = "Residuos y Aseo")
            )
        }
    }

    var selectedOperador by remember {
        mutableStateOf(operadoresList.firstOrNull() ?: Usuario(id = 3L, nombre = "Cuadrilla 1"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admitir y Asignar Reporte", fontWeight = FontWeight.Bold) },
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
            // Vista previa del reporte
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VISTA PREVIA DEL REPORTE ${report.codigo}",
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = report.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        text = report.descripcion,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SimulatedMapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        centerAddressText = report.direccion
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Selección de Prioridad Oficial
            Text(
                text = "Asignar Prioridad Oficial *",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Prioridad.entries.forEach { prio ->
                    FilterChip(
                        selected = selectedPriority == prio,
                        onClick = { selectedPriority = prio },
                        label = {
                            Text(
                                text = prio.displayName,
                                fontWeight = if (selectedPriority == prio) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dropdown "Operador / Cuadrilla Responsable"
            Text(
                text = "Operador / Cuadrilla Responsable *",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedOperadorDropdown,
                onExpandedChange = { expandedOperadorDropdown = !expandedOperadorDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "${selectedOperador.nombre} ${selectedOperador.apellido ?: ""}".trim(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOperadorDropdown) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedOperadorDropdown,
                    onDismissRequest = { expandedOperadorDropdown = false }
                ) {
                    operadoresList.forEach { op ->
                        DropdownMenuItem(
                            text = { Text("${op.nombre} ${op.apellido ?: ""}".trim()) },
                            onClick = {
                                selectedOperador = op
                                expandedOperadorDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón "Confirmar Admisión y Enviar Reporte"
            Button(
                onClick = {
                    gestorViewModel.admitirYAsignar(
                        reporteId = report.id.toLongOrNull() ?: 101L,
                        operadorId = selectedOperador.id ?: 3L,
                        prioridad = selectedPriority.name,
                        observacion = "Admitido por Gestor con prioridad ${selectedPriority.displayName}",
                        onSuccess = onAdmitidoExitoso
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                enabled = accionState !is UiState.Loading
            ) {
                if (accionState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar Admisión y Enviar Reporte", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
