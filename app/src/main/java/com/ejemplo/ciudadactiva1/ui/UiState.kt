package com.ejemplo.ciudadactiva1.ui

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val errorMessage: String) : UiState<Nothing>
}
