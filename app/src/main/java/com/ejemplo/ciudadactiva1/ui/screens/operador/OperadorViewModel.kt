package com.ejemplo.ciudadactiva1.ui.screens.operador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.Reporte
import com.ejemplo.ciudadactiva1.data.network.model.SeguimientoResponseDTO
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OperadorViewModel : ViewModel() {

    private val _misReportesAsignadosState = MutableStateFlow<UiState<List<Reporte>>>(UiState.Idle)
    val misReportesAsignadosState: StateFlow<UiState<List<Reporte>>> = _misReportesAsignadosState.asStateFlow()

    private val _avanceState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val avanceState: StateFlow<UiState<Boolean>> = _avanceState.asStateFlow()

    init {
        cargarReportesAsignados()
    }

    fun cargarReportesAsignados() {
        _misReportesAsignadosState.value = UiState.Loading
        val operadorId = SessionManager.getActiveUserId()

        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarReportes() }
            when (result) {
                is NetworkResult.Success -> {
                    val asignados = result.data.filter {
                        it.operador?.id == operadorId ||
                        it.estado.equals("EN_PROCESO", ignoreCase = true) ||
                        it.estado.equals("En Proceso", ignoreCase = true)
                    }
                    _misReportesAsignadosState.value = UiState.Success(asignados)
                }
                is NetworkResult.HttpError -> {
                    _misReportesAsignadosState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _misReportesAsignadosState.value = UiState.Error(result.throwable.message ?: "Error de red")
                }
            }
        }
    }

    fun registrarAvance(
        reporteId: Long,
        nuevoEstado: String,
        observaciones: String,
        evidenciaUrl: String? = null,
        onSuccess: () -> Unit
    ) {
        _avanceState.value = UiState.Loading
        val dto = SeguimientoResponseDTO(
            nuevoEstado = nuevoEstado,
            observaciones = observaciones,
            evidencia = evidenciaUrl
        )

        viewModelScope.launch {
            // POST /api/seguimientos/reportes/{id}/seguimientos
            safeApiCall { RetrofitClient.apiService.agregarSeguimientoAReporte(reporteId, dto) }

            // Sync MockRepository
            com.ejemplo.ciudadactiva1.data.mock.MockRepository.actualizarAvance(
                id = reporteId.toString(),
                transicionEstado = nuevoEstado,
                observaciones = observaciones,
                fotoAvanceUrl = evidenciaUrl
            )

            _avanceState.value = UiState.Success(true)
            cargarReportesAsignados()
            onSuccess()
        }
    }

    fun finalizarReporte(
        reporteId: Long,
        observacionesFinales: String,
        fotoDespuesUrl: String = "https://picsum.photos/400/300?random=88",
        onSuccess: () -> Unit
    ) {
        _avanceState.value = UiState.Loading
        val dto = SeguimientoResponseDTO(
            nuevoEstado = "ATENCION_FINALIZADA",
            observaciones = observacionesFinales,
            evidencia = fotoDespuesUrl
        )

        viewModelScope.launch {
            safeApiCall { RetrofitClient.apiService.agregarSeguimientoAReporte(reporteId, dto) }

            com.ejemplo.ciudadactiva1.data.mock.MockRepository.finalizarReportePorOperador(
                id = reporteId.toString(),
                fotoDespuesUrl = fotoDespuesUrl,
                observacionesFinales = observacionesFinales
            )

            _avanceState.value = UiState.Success(true)
            cargarReportesAsignados()
            onSuccess()
        }
    }
}
