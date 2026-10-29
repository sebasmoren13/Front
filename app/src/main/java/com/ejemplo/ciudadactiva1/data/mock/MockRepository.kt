package com.ejemplo.ciudadactiva1.data.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object MockRepository {

    val operadoresDisponibles = listOf(
        "Cuadrilla 1 - Vías y Pavimento",
        "Cuadrilla 2 - Alumbrado Público",
        "Cuadrilla 3 - Residuos y Aseo",
        "Cuadrilla 4 - Parques y Jardines",
        "Ing. Carlos Mendoza (Operador 1)",
        "Ing. Roberto Gómez (Operador 2)"
    )

    private val initialReportes = listOf(
        Reporte(
            id = "101",
            codigo = "#CA-101",
            titulo = "Bache en Avenida Central",
            descripcion = "Hoyo profundo en el carril derecho cerca del semáforo. Peligro para motocicletas.",
            categoria = Categoria.VIAS,
            prioridad = Prioridad.ALTA,
            estado = EstadoReporte.PENDIENTE,
            direccion = "Av. Central 452, Sector Norte",
            latitud = -12.046374,
            longitud = -77.042793,
            fechaRegistro = "12 Oct 2024, 09:30 AM",
            tiempoTranscurrido = "2.5 hrs",
            fotoUrl = "https://picsum.photos/400/300?random=1",
            usuarioId = "user_ana",
            timelineSteps = listOf(
                PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana - GPS (-12.046, -77.042)", "12 Oct 09:30 AM", EstadoTimelineStep.COMPLETED, "Ana"),
                PasoLineaTiempo(2, "Validación y Admisión", "Pendiente de revisión por Gestor", "En espera", EstadoTimelineStep.ACTIVE, "Gestor"),
                PasoLineaTiempo(3, "Asignación a Cuadrilla", "Pendiente de asignación", "En espera", EstadoTimelineStep.PENDING),
                PasoLineaTiempo(4, "Avance Registrado", "Sin avances aún", "En espera", EstadoTimelineStep.PENDING),
                PasoLineaTiempo(5, "Cierre y Confirmación Final", "Pendiente de finalización", "En espera", EstadoTimelineStep.PENDING)
            )
        ),
        Reporte(
            id = "102",
            codigo = "#CA-102",
            titulo = "Luminaria apagada en Parque Sur",
            descripcion = "El poste #402 frente a la cancha de básquetbol lleva 3 días sin funcionar.",
            categoria = Categoria.ALUMBRADO,
            prioridad = Prioridad.MEDIA,
            estado = EstadoReporte.EN_PROCESO,
            direccion = "Calle Las Flores 120, Parque Sur",
            latitud = -12.049374,
            longitud = -77.039793,
            fechaRegistro = "11 Oct 2024, 04:15 PM",
            tiempoTranscurrido = "18.2 hrs",
            fotoUrl = "https://picsum.photos/400/300?random=2",
            operadorAsignado = "Cuadrilla 2 - Alumbrado Público",
            observacionesAvance = "Inspección completada. Se solicitó cambio de balastro y foco LED de 100W.",
            usuarioId = "user_ana",
            timelineSteps = listOf(
                PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana - GPS (-12.049, -77.039)", "11 Oct 04:15 PM", EstadoTimelineStep.COMPLETED, "Ana"),
                PasoLineaTiempo(2, "Validación y Admisión", "Admitido por Gestor de Turno. Prioridad: Media", "11 Oct 05:00 PM", EstadoTimelineStep.COMPLETED, "Gestor Carlos"),
                PasoLineaTiempo(3, "Asignación a Cuadrilla", "Asignado a Cuadrilla 2 - Alumbrado Público", "11 Oct 05:30 PM", EstadoTimelineStep.COMPLETED, "Cuadrilla 2"),
                PasoLineaTiempo(4, "Avance Registrado", "Inspección técnica realizada, esperando repuesto", "12 Oct 08:00 AM", EstadoTimelineStep.ACTIVE, "Ing. Carlos Mendoza"),
                PasoLineaTiempo(5, "Cierre y Confirmación Final", "Pendiente de trabajo de campo final", "En espera", EstadoTimelineStep.PENDING)
            )
        ),
        Reporte(
            id = "103",
            codigo = "#CA-103",
            titulo = "Acumulación de escombros en acera",
            descripcion = "Restos de remodelación bloqueando el tránsito peatonal y la ciclovía.",
            categoria = Categoria.RESIDUOS,
            prioridad = Prioridad.ALTA,
            estado = EstadoReporte.EN_PROCESO,
            direccion = "Av. Los Olivos 890, Frente a la plaza",
            latitud = -12.043374,
            longitud = -77.045793,
            fechaRegistro = "12 Oct 2024, 07:00 AM",
            tiempoTranscurrido = "5.0 hrs",
            fotoUrl = "https://picsum.photos/400/300?random=3",
            operadorAsignado = "Cuadrilla 3 - Residuos y Aseo",
            observacionesAvance = "Camión recolector de carga pesada despachado al sitio.",
            usuarioId = "user_ana",
            timelineSteps = listOf(
                PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana - GPS (-12.043, -77.045)", "12 Oct 07:00 AM", EstadoTimelineStep.COMPLETED, "Ana"),
                PasoLineaTiempo(2, "Validación y Admisión", "Admitido con prioridad Alta por peligro vial", "12 Oct 07:30 AM", EstadoTimelineStep.COMPLETED, "Gestor Maria"),
                PasoLineaTiempo(3, "Asignación a Cuadrilla", "Asignado a Cuadrilla 3 - Residuos y Aseo", "12 Oct 08:00 AM", EstadoTimelineStep.COMPLETED, "Cuadrilla 3"),
                PasoLineaTiempo(4, "Avance Registrado", "Unidad de remoción en trayecto", "12 Oct 09:15 AM", EstadoTimelineStep.ACTIVE, "Operador"),
                PasoLineaTiempo(5, "Cierre y Confirmación Final", "Pendiente de recolección total", "En espera", EstadoTimelineStep.PENDING)
            )
        ),
        Reporte(
            id = "104",
            codigo = "#CA-104",
            titulo = "Reparación de juegos infantiles en Parque Central",
            descripcion = "Columpio roto con bordes metálicos filosos. Reparado y soldado exitosamente.",
            categoria = Categoria.PARQUES,
            prioridad = Prioridad.BAJA,
            estado = EstadoReporte.RESUELTO,
            direccion = "Jirón San Martín 340, Parque Central",
            latitud = -12.041374,
            longitud = -77.048793,
            fechaRegistro = "10 Oct 2024, 10:00 AM",
            tiempoTranscurrido = "Resuelto en 24 hrs",
            fotoUrl = "https://picsum.photos/400/300?random=4",
            fotoDespuesUrl = "https://picsum.photos/400/300?random=14",
            operadorAsignado = "Cuadrilla 4 - Parques y Jardines",
            observacionesAvance = "Estructura soldada y repintada con pintura anticorrosiva.",
            observacionesCierreOperador = "Trabajo concluido satisfactoriamente. Zona de juegos segura.",
            comentariosCierreCiudadano = "Excelente trabajo y rápida atención. ¡Muchas gracias!",
            valoracionEstrellas = 5,
            usuarioId = "user_ana",
            timelineSteps = listOf(
                PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana - GPS (-12.041, -77.048)", "10 Oct 10:00 AM", EstadoTimelineStep.COMPLETED, "Ana"),
                PasoLineaTiempo(2, "Validación y Admisión", "Admitido por Gestor de Turno", "10 Oct 10:45 AM", EstadoTimelineStep.COMPLETED, "Gestor"),
                PasoLineaTiempo(3, "Asignación a Cuadrilla", "Asignado a Cuadrilla 4 - Parques y Jardines", "10 Oct 11:30 AM", EstadoTimelineStep.COMPLETED, "Cuadrilla 4"),
                PasoLineaTiempo(4, "Avance Registrado", "Soldadura y pintura aplicadas", "10 Oct 03:00 PM", EstadoTimelineStep.COMPLETED, "Ing. Roberto Gómez"),
                PasoLineaTiempo(5, "Cierre y Confirmación Final", "Confirmado por el ciudadano con 5 estrellas", "11 Oct 10:00 AM", EstadoTimelineStep.COMPLETED, "Ana")
            )
        ),
        Reporte(
            id = "105",
            codigo = "#CA-105",
            titulo = "Solicitud de pintura en propiedad privada",
            descripcion = "Petición para pintar la fachada de un domicilio particular.",
            categoria = Categoria.VIAS,
            prioridad = Prioridad.BAJA,
            estado = EstadoReporte.RECHAZADO,
            direccion = "Calle Sol 123",
            latitud = -12.048374,
            longitud = -77.040793,
            fechaRegistro = "09 Oct 2024, 02:20 PM",
            tiempoTranscurrido = "Rechazado",
            fotoUrl = "https://picsum.photos/400/300?random=5",
            justificacionRechazo = "La solicitud corresponde a una propiedad privada y está fuera del alcance de los servicios de mantenimiento municipal del espacio público.",
            usuarioId = "user_ana",
            timelineSteps = listOf(
                PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana", "09 Oct 02:20 PM", EstadoTimelineStep.COMPLETED, "Ana"),
                PasoLineaTiempo(2, "Validación y Admisión", "Reporte rechazado por fuera de jurisdicción municipal", "09 Oct 03:00 PM", EstadoTimelineStep.COMPLETED, "Gestor Principal"),
                PasoLineaTiempo(3, "Asignación a Cuadrilla", "No aplica", "-", EstadoTimelineStep.PENDING),
                PasoLineaTiempo(4, "Avance Registrado", "No aplica", "-", EstadoTimelineStep.PENDING),
                PasoLineaTiempo(5, "Cierre y Confirmación Final", "Caso cerrado por rechazo", "09 Oct 03:00 PM", EstadoTimelineStep.COMPLETED)
            )
        )
    )

    private val _reportesFlow = MutableStateFlow<List<Reporte>>(initialReportes)
    val reportesFlow: StateFlow<List<Reporte>> = _reportesFlow.asStateFlow()

    fun getReportes(): List<Reporte> = _reportesFlow.value

    fun getReporteById(id: String): Reporte? {
        return _reportesFlow.value.find { it.id == id }
    }

    fun crearReporte(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        direccion: String,
        fotoUrl: String
    ): Reporte {
        val nextId = (System.currentTimeMillis() % 10000).toString()
        val nextCode = "#CA-${(100..999).random()}"
        val nuevoPaso1 = PasoLineaTiempo(1, "Registro Inicial", "Reporte creado por Ana - GPS (-12.045, -77.043)", "Hoy, Justo ahora", EstadoTimelineStep.COMPLETED, "Ana")
        val nuevoPaso2 = PasoLineaTiempo(2, "Validación y Admisión", "Pendiente de evaluación por el Gestor", "En espera", EstadoTimelineStep.ACTIVE, "Gestor")
        val nuevoPaso3 = PasoLineaTiempo(3, "Asignación a Cuadrilla", "Pendiente de asignación", "En espera", EstadoTimelineStep.PENDING)
        val nuevoPaso4 = PasoLineaTiempo(4, "Avance Registrado", "Sin avances aún", "En espera", EstadoTimelineStep.PENDING)
        val nuevoPaso5 = PasoLineaTiempo(5, "Cierre y Confirmación Final", "Pendiente", "En espera", EstadoTimelineStep.PENDING)

        val nuevo = Reporte(
            id = nextId,
            codigo = nextCode,
            titulo = titulo.ifBlank { "Reporte de ${categoria.displayName}" },
            descripcion = descripcion,
            categoria = categoria,
            prioridad = Prioridad.MEDIA,
            estado = EstadoReporte.PENDIENTE,
            direccion = direccion.ifBlank { "Av. Principal 123" },
            latitud = -12.045,
            longitud = -77.043,
            fechaRegistro = "Hoy, Justo ahora",
            tiempoTranscurrido = "0 min",
            fotoUrl = fotoUrl.ifBlank { "https://picsum.photos/400/300?random=10" },
            timelineSteps = listOf(nuevoPaso1, nuevoPaso2, nuevoPaso3, nuevoPaso4, nuevoPaso5),
            usuarioId = "user_ana"
        )

        _reportesFlow.value = listOf(nuevo) + _reportesFlow.value
        return nuevo
    }

    fun admitirYAsignarReporte(id: String, prioridad: Prioridad, operador: String) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    when (step.pasoNumero) {
                        2 -> step.copy(
                            detalle = "Admitido por Gestor. Prioridad asignada: ${prioridad.displayName}",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.COMPLETED
                        )
                        3 -> step.copy(
                            detalle = "Asignado a $operador",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.ACTIVE,
                            responsable = operador
                        )
                        else -> step
                    }
                }
                rep.copy(
                    prioridad = prioridad,
                    operadorAsignado = operador,
                    estado = EstadoReporte.EN_PROCESO,
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }

    fun rechazarReporte(id: String, justificacion: String) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    when (step.pasoNumero) {
                        2 -> step.copy(
                            detalle = "Rechazado por Gestor. Motivo: $justificacion",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.COMPLETED
                        )
                        5 -> step.copy(
                            detalle = "Caso cerrado por rechazo",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.COMPLETED
                        )
                        else -> step.copy(estado = EstadoTimelineStep.PENDING)
                    }
                }
                rep.copy(
                    estado = EstadoReporte.RECHAZADO,
                    justificacionRechazo = justificacion,
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }

    fun actualizarAvance(id: String, transicionEstado: String, observaciones: String, fotoAvanceUrl: String?) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    if (step.pasoNumero == 4) {
                        step.copy(
                            detalle = "$transicionEstado: $observaciones",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.ACTIVE
                        )
                    } else step
                }
                rep.copy(
                    observacionesAvance = observaciones,
                    fotoDespuesUrl = fotoAvanceUrl ?: rep.fotoDespuesUrl,
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }

    fun finalizarReportePorOperador(id: String, fotoDespuesUrl: String, observacionesFinales: String) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    when (step.pasoNumero) {
                        4 -> step.copy(
                            detalle = "Atención finalizada por operador: $observacionesFinales",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.COMPLETED
                        )
                        5 -> step.copy(
                            detalle = "Pendiente de confirmación del ciudadano",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.ACTIVE
                        )
                        else -> step
                    }
                }
                rep.copy(
                    estado = EstadoReporte.RESUELTO,
                    fotoDespuesUrl = fotoDespuesUrl.ifBlank { "https://picsum.photos/400/300?random=20" },
                    observacionesCierreOperador = observacionesFinales,
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }

    fun confirmarCierreCiudadano(id: String, estrellas: Int, comentarios: String) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    if (step.pasoNumero == 5) {
                        step.copy(
                            detalle = "Cierre verificado por ciudadano ($estrellas estrellas)",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.COMPLETED
                        )
                    } else step
                }
                rep.copy(
                    valoracionEstrellas = estrellas,
                    comentariosCierreCiudadano = comentarios,
                    estado = EstadoReporte.RESUELTO,
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }

    fun objetarReabrirCaso(id: String, comentarios: String) {
        _reportesFlow.value = _reportesFlow.value.map { rep ->
            if (rep.id == id) {
                val updatedSteps = rep.timelineSteps.map { step ->
                    when (step.pasoNumero) {
                        4 -> step.copy(
                            detalle = "Caso reabierto por ciudadano: $comentarios",
                            fecha = "Hoy, Reciente",
                            estado = EstadoTimelineStep.ACTIVE
                        )
                        5 -> step.copy(
                            detalle = "Reabierto - En revisión",
                            fecha = "Pendiente",
                            estado = EstadoTimelineStep.PENDING
                        )
                        else -> step
                    }
                }
                rep.copy(
                    estado = EstadoReporte.EN_PROCESO,
                    comentariosCierreCiudadano = "Objetado: $comentarios",
                    timelineSteps = updatedSteps
                )
            } else rep
        }
    }
}
