package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.ComentarioRequestDTO
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CierreIncidenciaViewModel : ViewModel() {

    private val _cierreState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val cierreState: StateFlow<UiState<Boolean>> = _cierreState.asStateFlow()

    fun cerrarReporte(
        reporteId: Long,
        comentarios: String,
        estrellas: Int,
        onSuccess: () -> Unit
    ) {
        _cierreState.value = UiState.Loading
        val usuarioId = SessionManager.getActiveUserId()

        viewModelScope.launch {
            // PATCH /api/reportes/{id}/cerrar
            val cerrarResult = safeApiCall { RetrofitClient.apiService.cerrarReporte(reporteId) }

            // POST /api/comentarios
            val comentarioDto = ComentarioRequestDTO(
                contenido = "Calificación ($estrellas/5 estrellas): $comentarios",
                reporteId = reporteId,
                usuarioId = usuarioId
            )
            safeApiCall { RetrofitClient.apiService.crearComentario(comentarioDto) }

            // Also update MockRepository for offline / preview display
            com.ejemplo.ciudadactiva1.data.mock.MockRepository.confirmarCierreCiudadano(
                id = reporteId.toString(),
                estrellas = estrellas,
                comentarios = comentarios
            )

            _cierreState.value = UiState.Success(true)
            onSuccess()
        }
    }

    fun objetarReabrirCaso(
        reporteId: Long,
        comentarios: String,
        onSuccess: () -> Unit
    ) {
        _cierreState.value = UiState.Loading
        val usuarioId = SessionManager.getActiveUserId()

        viewModelScope.launch {
            val objecionDto = ComentarioRequestDTO(
                contenido = "[OBJECIÓN DE CIUDADANO] $comentarios",
                reporteId = reporteId,
                usuarioId = usuarioId
            )
            safeApiCall { RetrofitClient.apiService.crearComentario(objecionDto) }

            // Update MockRepository locally
            com.ejemplo.ciudadactiva1.data.mock.MockRepository.objetarReabrirCaso(
                id = reporteId.toString(),
                comentarios = comentarios
            )

            _cierreState.value = UiState.Success(true)
            onSuccess()
        }
    }
}
