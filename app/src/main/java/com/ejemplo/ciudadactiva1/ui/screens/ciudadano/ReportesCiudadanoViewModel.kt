package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.Reporte
import com.ejemplo.ciudadactiva1.data.network.model.Seguimiento
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportesCiudadanoViewModel : ViewModel() {

    private val _reportesState = MutableStateFlow<UiState<List<Reporte>>>(UiState.Idle)
    val reportesState: StateFlow<UiState<List<Reporte>>> = _reportesState.asStateFlow()

    private val _misReportesState = MutableStateFlow<UiState<List<Reporte>>>(UiState.Idle)
    val misReportesState: StateFlow<UiState<List<Reporte>>> = _misReportesState.asStateFlow()

    private val _seguimientosState = MutableStateFlow<UiState<List<Seguimiento>>>(UiState.Idle)
    val seguimientosState: StateFlow<UiState<List<Seguimiento>>> = _seguimientosState.asStateFlow()

    fun cargarReportes() {
        _reportesState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarReportes() }
            when (result) {
                is NetworkResult.Success -> {
                    _reportesState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _reportesState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _reportesState.value = UiState.Error(result.throwable.message ?: "Error de conexión")
                }
            }
        }
    }

    fun cargarMisReportes() {
        val ciudadanoId = SessionManager.getActiveUserId()
        _misReportesState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.obtenerPorCiudadano(ciudadanoId) }
            when (result) {
                is NetworkResult.Success -> {
                    _misReportesState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _misReportesState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _misReportesState.value = UiState.Error(result.throwable.message ?: "Error de conexión")
                }
            }
        }
    }

    fun cargarSeguimientos(reporteId: Long) {
        _seguimientosState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarSeguimientosPorReporte(reporteId) }
            when (result) {
                is NetworkResult.Success -> {
                    _seguimientosState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _seguimientosState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _seguimientosState.value = UiState.Error(result.throwable.message ?: "Error de conexión")
                }
            }
        }
    }
}
