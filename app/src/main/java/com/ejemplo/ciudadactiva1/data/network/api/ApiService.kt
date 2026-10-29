package com.ejemplo.ciudadactiva1.data.network.api

import com.ejemplo.ciudadactiva1.data.network.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDTO
    ): Response<ResponseBody>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDTO
    ): Response<ResponseBody>

    // --- USUARIOS ---
    @GET("api/usuarios/me")
    suspend fun obtenerUsuarioActual(): Response<UsuarioResponseDTO>

    @GET("api/usuarios")
    suspend fun listarUsuarios(): Response<List<Usuario>>

    // --- CATEGORIAS ---
    @GET("api/categorias")
    suspend fun listarCategorias(): Response<List<CategoriaIncidencia>>

    @POST("api/categorias")
    suspend fun crearCategoria(
        @Body categoria: CategoriaIncidencia
    ): Response<CategoriaIncidencia>

    // --- REPORTES ---
    @GET("api/reportes")
    suspend fun listarReportes(): Response<List<Reporte>>

    @POST("api/reportes")
    suspend fun registrarReporte(
        @Body dto: ReporteResponseDTO
    ): Response<Reporte>

    @GET("api/reportes/{id}")
    suspend fun obtenerReportePorId(
        @Path("id") id: Long
    ): Response<Reporte>

    @GET("api/reportes/ciudadano/{id}")
    suspend fun obtenerPorCiudadano(
        @Path("id") id: Long
    ): Response<List<Reporte>>

    @POST("api/reportes/{id}/asignaciones")
    suspend fun asignarResponsable(
        @Path("id") id: Long,
        @Body dto: AsignacionRequestDTO
    ): Response<Reporte>

    @PATCH("api/reportes/{id}/validar")
    suspend fun validarReporte(
        @Path("id") id: Long,
        @Body dto: ValidarReporteRequestDTO
    ): Response<Reporte>

    @PATCH("api/reportes/{id}/cerrar")
    suspend fun cerrarReporte(
        @Path("id") id: Long
    ): Response<Reporte>

    // --- SEGUIMIENTOS ---
    @POST("api/seguimientos/reportes/{id}/seguimientos")
    suspend fun agregarSeguimientoAReporte(
        @Path("id") id: Long,
        @Body dto: SeguimientoResponseDTO
    ): Response<SeguimientoResponseDTO>

    @GET("api/seguimientos/reporte/{reporteId}")
    suspend fun listarSeguimientosPorReporte(
        @Path("reporteId") reporteId: Long
    ): Response<List<Seguimiento>>

    // --- COMENTARIOS ---
    @POST("api/comentarios")
    suspend fun crearComentario(
        @Body dto: ComentarioRequestDTO
    ): Response<Map<String, Any>>

    @GET("api/comentarios/reporte/{reporteId}")
    suspend fun listarComentariosPorReporte(
        @Path("reporteId") reporteId: Long
    ): Response<Any>

    // --- ESTADISTICAS ---
    @GET("api/estadisticas/reportes")
    suspend fun obtenerEstadisticasReportes(): Response<Map<String, Any>>

    // --- EVIDENCIAS ---
    @Multipart
    @POST("api/evidencias/subir")
    suspend fun subirEvidencia(
        @Query("reporteId") reporteId: Long,
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>

    @GET("api/evidencias/reporte/{reporteId}")
    suspend fun listarEvidenciasPorReporte(
        @Path("reporteId") reporteId: Long
    ): Response<Any>
}
