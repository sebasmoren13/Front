package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.CategoriaIncidencia
import com.ejemplo.ciudadactiva1.data.network.model.Reporte
import com.ejemplo.ciudadactiva1.data.network.model.ReporteResponseDTO
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CrearReporteViewModel : ViewModel() {

    private val _categoriasState = MutableStateFlow<UiState<List<CategoriaIncidencia>>>(UiState.Idle)
    val categoriasState: StateFlow<UiState<List<CategoriaIncidencia>>> = _categoriasState.asStateFlow()

    private val _crearReporteState = MutableStateFlow<UiState<Reporte>>(UiState.Idle)
    val crearReporteState: StateFlow<UiState<Reporte>> = _crearReporteState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        _categoriasState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarCategorias() }
            when (result) {
                is NetworkResult.Success -> {
                    _categoriasState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _categoriasState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _categoriasState.value = UiState.Error(result.throwable.message ?: "Error de red")
                }
            }
        }
    }

    fun enviarReporte(
        titulo: String,
        descripcion: String,
        ubicacion: String,
        categoriaId: Long,
        prioridad: String = "MEDIA",
        imageBytes: ByteArray? = null,
        onSuccess: () -> Unit
    ) {
        _crearReporteState.value = UiState.Loading
        val ciudadanoId = SessionManager.getActiveUserId()

        val dto = ReporteResponseDTO(
            titulo = titulo,
            descripcion = descripcion,
            ubicacion = ubicacion,
            prioridad = prioridad,
            ciudadanoId = ciudadanoId,
            categoriaId = categoriaId
        )

        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.registrarReporte(dto) }
            when (result) {
                is NetworkResult.Success -> {
                    val reporteCreado = result.data
                    reporteCreado.id?.let { reporteId ->
                        if (imageBytes != null) {
                            subirEvidencia(reporteId, imageBytes)
                        }
                    }
                    _crearReporteState.value = UiState.Success(reporteCreado)
                    onSuccess()
                }
                is NetworkResult.HttpError -> {
                    val err = "Error al enviar reporte (${result.code}): ${result.message ?: "Error de servidor / No autorizado (403)"}"
                    _crearReporteState.value = UiState.Error(err)
                }
                is NetworkResult.Exception -> {
                    val err = "Error de conexión al enviar reporte: ${result.throwable.message ?: "Sin respuesta"}"
                    _crearReporteState.value = UiState.Error(err)
                }
            }
        }
    }

    private suspend fun subirEvidencia(reporteId: Long, imageBytes: ByteArray) {
        try {
            val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "evidencia.jpg", requestFile)
            safeApiCall { RetrofitClient.apiService.subirEvidencia(reporteId, body) }
        } catch (_: Exception) { }
    }
}
