package com.ejemplo.ciudadactiva1.data.network

import com.ejemplo.ciudadactiva1.data.mock.Role
import com.ejemplo.ciudadactiva1.data.mock.UserSession
import com.ejemplo.ciudadactiva1.data.network.model.UsuarioResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private val _usuarioDto = MutableStateFlow<UsuarioResponseDTO?>(null)
    val usuarioDto: StateFlow<UsuarioResponseDTO?> = _usuarioDto.asStateFlow()

    var authToken: String? = null

    fun setSession(dto: UsuarioResponseDTO, rawToken: String? = null, role: Role = Role.CIUDADANO) {
        val cleanToken = rawToken?.trim()?.removeSurrounding("\"")?.trim()
        if (!cleanToken.isNullOrBlank()) {
            authToken = cleanToken
        }

        val userName = dto.nombre?.ifBlank { "Usuario" } ?: "Usuario"
        val userEmail = dto.email ?: ""

        val initials = userName.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifBlank { "U" }

        val session = UserSession(
            id = (dto.id ?: 1L).toString(),
            name = userName,
            email = userEmail,
            role = role,
            avatarInitials = initials
        )

        _currentUser.value = session
        _usuarioDto.value = dto
    }

    fun clearSession() {
        _currentUser.value = null
        _usuarioDto.value = null
        authToken = null
    }

    fun getActiveUserId(): Long {
        return _usuarioDto.value?.id
            ?: _currentUser.value?.id?.filter { it.isDigit() }?.toLongOrNull()
            ?: 1L
    }
}
