package com.ejemplo.ciudadactiva1.data.mock

data class UserSession(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val avatarInitials: String
)
