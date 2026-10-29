package com.ejemplo.ciudadactiva1.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.ciudadactiva1.data.mock.Role
import com.ejemplo.ciudadactiva1.data.mock.UserSession
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.client.RetrofitClient
import com.ejemplo.ciudadactiva1.data.network.model.LoginRequestDTO
import com.ejemplo.ciudadactiva1.data.network.model.RegisterRequestDTO
import com.ejemplo.ciudadactiva1.data.network.model.UsuarioResponseDTO
import com.ejemplo.ciudadactiva1.data.network.result.NetworkResult
import com.ejemplo.ciudadactiva1.data.network.result.safeApiCall
import com.ejemplo.ciudadactiva1.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<UserSession>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserSession>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val registerState: StateFlow<UiState<Boolean>> = _registerState.asStateFlow()

    val currentUser: StateFlow<UserSession?> = SessionManager.currentUser

    private val _selectedRole = MutableStateFlow(Role.CIUDADANO)
    val selectedRole: StateFlow<Role> = _selectedRole.asStateFlow()

    private val _email = MutableStateFlow("ana@ciudadano.com")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("123456")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onRoleSelected(role: Role) {
        _selectedRole.value = role
        _errorMessage.value = null
        when (role) {
            Role.CIUDADANO -> _email.value = "ana@ciudadano.com"
            Role.GESTOR -> _email.value = "gestor@gestor.com"
            Role.OPERADOR -> _email.value = "operador@operador.com"
            Role.ADMINISTRADOR -> _email.value = "admin@admin.com"
        }
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _errorMessage.value = null
    }

    private fun validateEmailDomain(email: String, role: Role): String? {
        val trimmed = email.trim().lowercase()
        if (trimmed.isBlank()) return "Por favor ingrese un correo electrónico."

        return when (role) {
            Role.GESTOR -> if (!trimmed.endsWith("@gestor.com")) "Acceso denegado: El correo para el rol Gestor debe terminar EXACTAMENTE en '@gestor.com'." else null
            Role.OPERADOR -> if (!trimmed.endsWith("@operador.com")) "Acceso denegado: El correo para el rol Operador debe terminar EXACTAMENTE en '@operador.com'." else null
            Role.ADMINISTRADOR -> if (!trimmed.endsWith("@admin.com")) "Acceso denegado: El correo para el rol Administrador debe terminar EXACTAMENTE en '@admin.com'." else null
            Role.CIUDADANO -> if (!trimmed.contains("@")) "Ingrese un correo electrónico válido." else null
        }
    }

    fun login(onSuccess: (Role) -> Unit) {
        val currentEmail = _email.value.trim()
        val currentRole = _selectedRole.value

        // Validación estricta por dominio
        val validationError = validateEmailDomain(currentEmail, currentRole)
        if (validationError != null) {
            _errorMessage.value = validationError
            _loginState.value = UiState.Error(validationError)
            return
        }

        _loginState.value = UiState.Loading
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = safeApiCall {
                    RetrofitClient.apiService.login(LoginRequestDTO(currentEmail, _password.value))
                }

                when (result) {
                    is NetworkResult.Success -> {
                        val respuestaToken = result.data.string().trim().removeSurrounding("\"").trim()
                        SessionManager.authToken = respuestaToken

                        // Petición inmediata a GET /api/usuarios/me con el token
                        val meResult = safeApiCall { RetrofitClient.apiService.obtenerUsuarioActual() }
                        val userDto = when (meResult) {
                            is NetworkResult.Success -> meResult.data
                            else -> UsuarioResponseDTO(
                                id = when (currentRole) {
                                    Role.CIUDADANO -> 1L
                                    Role.GESTOR -> 2L
                                    Role.OPERADOR -> 3L
                                    Role.ADMINISTRADOR -> 4L
                                },
                                nombre = currentEmail,
                                email = currentEmail,
                                rol = currentRole.name
                            )
                        }

                        SessionManager.setSession(userDto, respuestaToken, currentRole)

                        val activeSession = SessionManager.currentUser.value
                        if (activeSession != null) {
                            _loginState.value = UiState.Success(activeSession)
                            _errorMessage.value = null
                            onSuccess(currentRole)
                        } else {
                            val err = "No se pudo recuperar la información de sesión del usuario."
                            _errorMessage.value = err
                            _loginState.value = UiState.Error(err)
                        }
                    }
                    is NetworkResult.HttpError -> {
                        val err = "Error de autenticación (${result.code}): Usuario o contraseña incorrectos."
                        _errorMessage.value = err
                        _loginState.value = UiState.Error(err)
                    }
                    is NetworkResult.Exception -> {
                        val err = "Error de red al conectar con el servidor: ${result.throwable.message ?: "Sin respuesta"}"
                        _errorMessage.value = err
                        _loginState.value = UiState.Error(err)
                    }
                }
            } catch (e: Exception) {
                val err = "Error de conexión al servidor: ${e.message ?: "Error inesperado"}"
                _errorMessage.value = err
                _loginState.value = UiState.Error(err)
            }
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        contraseña: String,
        onSuccess: () -> Unit
    ) {
        if (nombre.isBlank() || correo.isBlank() || contraseña.isBlank()) {
            _errorMessage.value = "Por favor complete todos los campos obligatorios."
            _registerState.value = UiState.Error("Por favor complete todos los campos obligatorios.")
            return
        }

        _registerState.value = UiState.Loading
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val dto = RegisterRequestDTO(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo,
                    contraseña = contraseña,
                    telefono = telefono,
                    rolId = 1L
                )

                val result = safeApiCall { RetrofitClient.apiService.register(dto) }

                when (result) {
                    is NetworkResult.Success -> {
                        _registerState.value = UiState.Success(true)
                        _errorMessage.value = null
                        onSuccess()
                    }
                    is NetworkResult.HttpError -> {
                        val err = "Error de registro (${result.code}): ${result.message ?: "No se pudo crear la cuenta"}"
                        _errorMessage.value = err
                        _registerState.value = UiState.Error(err)
                    }
                    is NetworkResult.Exception -> {
                        val err = "Error de conexión al registrar: ${result.throwable.message ?: "Error de red"}"
                        _errorMessage.value = err
                        _registerState.value = UiState.Error(err)
                    }
                }
            } catch (e: Exception) {
                val err = "Error de conexión al servidor: ${e.message ?: "Error inesperado"}"
                _errorMessage.value = err
                _registerState.value = UiState.Error(err)
            }
        }
    }

    fun logout() {
        SessionManager.clearSession()
        _loginState.value = UiState.Idle
        _registerState.value = UiState.Idle
        _errorMessage.value = null
    }
}
