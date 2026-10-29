package com.ejemplo.ciudadactiva1.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.Reporte
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val _estadisticasState = MutableStateFlow<UiState<Map<String, Any>>>(UiState.Idle)
    val estadisticasState: StateFlow<UiState<Map<String, Any>>> = _estadisticasState.asStateFlow()

    private val _reportesActivosState = MutableStateFlow<UiState<List<Reporte>>>(UiState.Idle)
    val reportesActivosState: StateFlow<UiState<List<Reporte>>> = _reportesActivosState.asStateFlow()

    init {
        cargarEstadisticas()
        cargarReportesActivos()
    }

    fun cargarEstadisticas() {
        _estadisticasState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.obtenerEstadisticasReportes() }
            when (result) {
                is NetworkResult.Success -> {
                    _estadisticasState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _estadisticasState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _estadisticasState.value = UiState.Error(result.throwable.message ?: "Error de red")
                }
            }
        }
    }

    fun cargarReportesActivos() {
        _reportesActivosState.value = UiState.Loading
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.apiService.listarReportes() }
            when (result) {
                is NetworkResult.Success -> {
                    _reportesActivosState.value = UiState.Success(result.data)
                }
                is NetworkResult.HttpError -> {
                    _reportesActivosState.value = UiState.Error("Error ${result.code}: ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _reportesActivosState.value = UiState.Error(result.throwable.message ?: "Error de red")
                }
            }
        }
    }
}
