package com.ejemplo.ciudadactiva1.data.network.model

import com.google.gson.annotations.SerializedName

data class CategoriaIncidencia(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("description") val description: String? = null
)

data class Rol(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String? = null
)

data class Usuario(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("contraseña") val contraseña: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("rol") val rol: Rol? = null,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = null
)

data class UsuarioResponseDTO(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("rol") val rol: String? = null
)

data class Reporte(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("titulo") val titulo: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("ubicacion") val ubicacion: String? = null,
    @SerializedName("imagen") val imagen: String? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("categoriaIncidencia") val categoriaIncidencia: CategoriaIncidencia? = null,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = null,
    @SerializedName("prioridad") val prioridad: String? = null,
    @SerializedName("ciudadano") val ciudadano: Usuario? = null,
    @SerializedName("operador") val operador: Usuario? = null
)

data class ReporteResponseDTO(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("ubicacion") val ubicacion: String,
    @SerializedName("imagen") val imagen: String? = null,
    @SerializedName("prioridad") val prioridad: String? = null,
    @SerializedName("ciudadanoId") val ciudadanoId: Long,
    @SerializedName("categoriaId") val categoriaId: Long
)

data class Seguimiento(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("observaciones") val observaciones: String? = null,
    @SerializedName("estadoAnterior") val estadoAnterior: String? = null,
    @SerializedName("estadoNuevo") val estadoNuevo: String? = null,
    @SerializedName("reporte") val reporte: Reporte? = null,
    @SerializedName("fechaCambio") val fechaCambio: String? = null
)

data class SeguimientoResponseDTO(
    @SerializedName("nuevoEstado") val nuevoEstado: String? = null,
    @SerializedName("observaciones") val observaciones: String? = null,
    @SerializedName("evidencia") val evidencia: String? = null
)

data class AsignacionRequestDTO(
    @SerializedName("operadorId") val operadorId: Long? = null,
    @SerializedName("observacion") val observacion: String? = null
)

data class ValidarReporteRequestDTO(
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("observacion") val observacion: String? = null,
    @SerializedName("justificacion") val justificacion: String? = null
)

data class ComentarioRequestDTO(
    @SerializedName("contenido") val contenido: String? = null,
    @SerializedName("reporteId") val reporteId: Long? = null,
    @SerializedName("usuarioId") val usuarioId: Long? = null
)

data class LoginRequestDTO(
    @SerializedName("correo") val correo: String,
    @SerializedName("contraseña") val contraseña: String
)

data class RegisterRequestDTO(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("contraseña") val contraseña: String,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("rolId") val rolId: Long? = null
)
