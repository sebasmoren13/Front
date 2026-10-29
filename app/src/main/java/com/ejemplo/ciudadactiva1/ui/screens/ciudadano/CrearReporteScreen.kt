package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ejemplo.ciudadactiva1.data.mock.Categoria
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.model.CategoriaIncidencia
import com.ejemplo.ciudadactiva1.ui.UiState
import com.ejemplo.ciudadactiva1.ui.components.DottedCameraBox
import com.ejemplo.ciudadactiva1.ui.components.SimulatedMapBox
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReporteScreen(
    crearReporteViewModel: CrearReporteViewModel = viewModel(),
    onBack: () -> Unit,
    onReporteCreado: () -> Unit
) {
    val context = LocalContext.current

    val categoriasState by crearReporteViewModel.categoriasState.collectAsState()
    val crearState by crearReporteViewModel.crearReporteState.collectAsState()

    var selectedCategory by remember { mutableStateOf(Categoria.VIAS) }
    var selectedCategoriaDtoId by remember { mutableLongStateOf(1L) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var descripcionText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val categoriesList = remember(categoriasState) {
        when (val state = categoriasState) {
            is UiState.Success -> state.data
            else -> listOf(
                CategoriaIncidencia(1L, "Vías", "Pistas, veredas y baches"),
                CategoriaIncidencia(2L, "Alumbrado", "Postes y focos malogrados"),
                CategoriaIncidencia(3L, "Residuos", "Basura y escombros acumulados"),
                CategoriaIncidencia(4L, "Parques", "Áreas verdes y juegos infantiles")
            )
        }
    }

    // Regla Obligatoria: El botón verde de "Enviar Reporte" DEBE estar deshabilitado si no hay imagen o si descripción/ubicación están vacías.
    val isFormValid = descripcionText.isNotBlank() && selectedImageUri != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Incidente", fontWeight = FontWeight.Bold) },
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
            // Dropdown Categoría
            Text(
                text = "Categoría del Incidente *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropdown,
                onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoryDropdown,
                    onDismissRequest = { expandedCategoryDropdown = false }
                ) {
                    categoriesList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre ?: "Categoría") },
                            onClick = {
                                selectedCategory = when (cat.id) {
                                    1L -> Categoria.VIAS
                                    2L -> Categoria.ALUMBRADO
                                    3L -> Categoria.RESIDUOS
                                    else -> Categoria.PARQUES
                                }
                                selectedCategoriaDtoId = cat.id ?: 1L
                                expandedCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OutlinedTextField Descripción del problema
            Text(
                text = "Descripción del problema *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = descripcionText,
                onValueChange = { descripcionText = it },
                placeholder = { Text("Detalle la situación, referencia exacta o peligro percibido...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Caja de Ubicación GPS
            Text(
                text = "Ubicación del Reporte (GPS Centrado) *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            SimulatedMapBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                centerAddressText = "Av. Javier Prado Este 234, Lima (-12.088, -77.012)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de Fotografía de Evidencia Obligatoria
            Text(
                text = "Fotografía de Evidencia (Obligatoria) *",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (selectedImageUri == null) MaterialTheme.colorScheme.error else ForestGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, ForestGreen, RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Vista previa de la evidencia",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "Toque para cambiar foto",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(topStart = 8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                DottedCameraBox(
                    textLabel = "Toca para abrir la galería y adjuntar foto *",
                    isPhotoAttached = false,
                    onClick = { imagePickerLauncher.launch("image/*") }
                )
            }

            if (crearState is UiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (crearState as UiState.Error).errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón verde "Enviar Reporte"
            Button(
                onClick = {
                    // Task 2: Verificación de Sesión antes de enviar
                    if (SessionManager.authToken.isNullOrBlank()) {
                        Toast.makeText(context, "Debe iniciar sesión nuevamente", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val imageBytes = selectedImageUri?.let { uri ->
                        try {
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        } catch (_: Exception) {
                            null
                        }
                    }

                    crearReporteViewModel.enviarReporte(
                        titulo = "Reporte de ${selectedCategory.displayName}",
                        descripcion = descripcionText,
                        ubicacion = "Av. Javier Prado Este 234",
                        categoriaId = selectedCategoriaDtoId,
                        prioridad = "MEDIA",
                        imageBytes = imageBytes,
                        onSuccess = onReporteCreado
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    disabledContainerColor = Color(0xFFA5C4B5)
                ),
                enabled = isFormValid && crearState !is UiState.Loading
            ) {
                if (crearState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri == null) "Adjunte una foto para enviar" else "Enviar Reporte",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
