package com.ejemplo.ciudadactiva1.data.mock

enum class Categoria(val displayName: String) {
    VIAS("Vías"),
    ALUMBRADO("Alumbrado"),
    RESIDUOS("Residuos"),
    PARQUES("Parques")
}

enum class Prioridad(val displayName: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

enum class EstadoReporte(val displayName: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En Proceso"),
    RESUELTO("Resuelto"),
    RECHAZADO("Rechazado")
}

enum class EstadoTimelineStep {
    COMPLETED,
    ACTIVE,
    PENDING
}

data class PasoLineaTiempo(
    val pasoNumero: Int,
    val titulo: String,
    val detalle: String,
    val fecha: String,
    val estado: EstadoTimelineStep,
    val responsable: String? = null
)

data class Reporte(
    val id: String,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoReporte,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val fechaRegistro: String,
    val tiempoTranscurrido: String,
    val fotoUrl: String,
    val fotoDespuesUrl: String? = null,
    val justificacionRechazo: String? = null,
    val operadorAsignado: String? = null,
    val observacionesAvance: String? = null,
    val observacionesCierreOperador: String? = null,
    val comentariosCierreCiudadano: String? = null,
    val valoracionEstrellas: Int? = null,
    val timelineSteps: List<PasoLineaTiempo> = emptyList(),
    val usuarioId: String = "user_ana"
)
