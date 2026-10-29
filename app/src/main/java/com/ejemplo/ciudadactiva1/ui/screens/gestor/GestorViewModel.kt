package com.ejemplo.ciudadactiva1.ui.screens.gestor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.AsignacionRequestDTO
import com.ejemplo.ciudadactiva1.data.network.model.Reporte
import com.ejemplo.ciudadactiva1.data.network.model.Usuario
import com.ejemplo.ciudadactiva1.data.network.model.ValidarReporteRequestDTO
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestorViewModel : ViewModel() {

    private val _pendientesState = MutableStateFlow<UiState<List<Reporte>>>(UiState.Idle)
    val pendientesState: StateFlow<UiState<List<Reporte>>> = _pendientesState.asStateFlow()

    private val _operadoresState = MutableStateFlow<UiState<List<Usuario>>>(UiState.Idle)
    val operadoresState: StateFlow<UiState<List<Usuario>>> = _operadoresState.asStateFlow()

    private val _accionState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val accionState: StateFlow<UiState<Boolean>> = _accionState.asStateFlow()

    init {
        cargarReportesPendientes()
        cargarOperadores()
    }

    fun cargarReportesPendientes() {
        _pendientesState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarReportes() }
            when (result) {
                is NetworkResult.Success -> {
                    val pendientes = result.data.filter {
                        it.estado.equals("PENDIENTE", ignoreCase = true) || it.estado.equals("Pendiente", ignoreCase = true)
                    }
                    _pendientesState.value = UiState.Success(pendientes)
                }
                is NetworkResult.HttpError -> {
                    _pendientesState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _pendientesState.value = UiState.Error(result.throwable.message ?: "Error de red")
                }
            }
        }
    }

    fun cargarOperadores() {
        _operadoresState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarUsuarios() }
            when (result) {
                is NetworkResult.Success -> {
                    val operadores = result.data.filter {
                        it.rol?.nombre.equals("OPERADOR", ignoreCase = true) ||
                        it.rol?.nombre.equals("Operador", ignoreCase = true)
                    }
                    _operadoresState.value = UiState.Success(operadores)
                }
                is NetworkResult.HttpError -> {
                    _operadoresState.value = UiState.Error("Error ${result.code}")
                }
                is NetworkResult.Exception -> {
                    _operadoresState.value = UiState.Error("Error de red")
                }
            }
        }
    }

    fun admitirYAsignar(
        reporteId: Long,
        operadorId: Long,
        prioridad: String,
        observacion: String,
        onSuccess: () -> Unit
    ) {
        _accionState.value = UiState.Loading
        viewModelScope.launch {
            // PATCH /api/reportes/{id}/validar
            val validarDto = ValidarReporteRequestDTO(
                estado = "EN_PROCESO",
                observacion = observacion
            )
            safeApiCall { RetrofitClient.apiService.validarReporte(reporteId, validarDto) }

            // POST /api/reportes/{id}/asignaciones
            val asignacionDto = AsignacionRequestDTO(
                operadorId = operadorId,
                observacion = observacion
            )
            safeApiCall { RetrofitClient.apiService.asignarResponsable(reporteId, asignacionDto) }

            // Sync local MockRepository
            val prioEnum = when (prioridad.uppercase()) {
                "ALTA" -> com.ejemplo.ciudadactiva1.data.mock.Prioridad.ALTA
                "BAJA" -> com.ejemplo.ciudadactiva1.data.mock.Prioridad.BAJA
                else -> com.ejemplo.ciudadactiva1.data.mock.Prioridad.MEDIA
            }
            com.ejemplo.ciudadactiva1.data.mock.MockRepository.admitirYAsignarReporte(
                id = reporteId.toString(),
                prioridad = prioEnum,
                operador = "Operador #$operadorId"
            )

            _accionState.value = UiState.Success(true)
            cargarReportesPendientes()
            onSuccess()
        }
    }

    fun rechazarReporte(
        reporteId: Long,
        justificacion: String,
        onSuccess: () -> Unit
    ) {
        _accionState.value = UiState.Loading
        viewModelScope.launch {
            // PATCH /api/reportes/{id}/validar
            val validarDto = ValidarReporteRequestDTO(
                estado = "RECHAZADO",
                observacion = justificacion,
                justificacion = justificacion
            )
            safeApiCall { RetrofitClient.apiService.validarReporte(reporteId, validarDto) }

            // Sync local MockRepository
            com.ejemplo.ciudadactiva1.data.mock.MockRepository.rechazarReporte(
                id = reporteId.toString(),
                justificacion = justificacion
            )

            _accionState.value = UiState.Success(true)
            cargarReportesPendientes()
            onSuccess()
        }
    }
}
