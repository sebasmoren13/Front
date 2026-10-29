package com.ejemplo.ciudadactiva1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ejemplo.ciudadactiva1.data.mock.Role
import com.ejemplo.ciudadactiva1.ui.components.BottomBarDestination
import com.ejemplo.ciudadactiva1.ui.screens.admin.AdminDashboardScreen
import com.ejemplo.ciudadactiva1.ui.screens.admin.IndicadoresScreen
import com.ejemplo.ciudadactiva1.ui.screens.admin.MonitoreoReportesActivosScreen
import com.ejemplo.ciudadactiva1.ui.screens.auth.AuthViewModel
import com.ejemplo.ciudadactiva1.ui.screens.auth.LoginScreen
import com.ejemplo.ciudadactiva1.ui.screens.auth.RegistroScreen
import com.ejemplo.ciudadactiva1.ui.screens.ciudadano.*
import com.ejemplo.ciudadactiva1.ui.screens.gestor.AdmitirAsignarScreen
import com.ejemplo.ciudadactiva1.ui.screens.gestor.BandejaEntradaScreen
import com.ejemplo.ciudadactiva1.ui.screens.operador.ActualizarAvanceScreen
import com.ejemplo.ciudadactiva1.ui.screens.operador.DetalleReporteAsignadoScreen
import com.ejemplo.ciudadactiva1.ui.screens.operador.MisReportesAsignadosScreen

object Routes {
    const val LOGIN = "auth_login"
    const val REGISTRO = "auth_registro"

    // Ciudadano Routes
    const val CIUDADANO_HOME = "ciudadano_home"
    const val CIUDADANO_NOTIFICACIONES = "ciudadano_notificaciones"
    const val CIUDADANO_PERFIL = "ciudadano_perfil"
    const val CIUDADANO_CREAR = "ciudadano_crear_reporte"
    const val CIUDADANO_INCIDENTES = "ciudadano_ver_incidentes"
    const val CIUDADANO_HISTORIAL = "ciudadano_historial"
    const val CIUDADANO_TIMELINE = "ciudadano_timeline/{reportId}"
    const val CIUDADANO_CIERRE = "ciudadano_cierre/{reportId}"

    // Gestor Routes
    const val GESTOR_BANDEJA = "gestor_bandeja"
    const val GESTOR_ADMITIR = "gestor_admitir/{reportId}"

    // Operador Routes
    const val OPERADOR_REPORTES = "operador_mis_reportes"
    const val OPERADOR_DETALLE = "operador_detalle/{reportId}"
    const val OPERADOR_AVANCE = "operador_actualizar_avance/{reportId}"

    // Admin Routes
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_INDICADORES = "admin_indicadores"
    const val ADMIN_MONITOREO = "admin_monitoreo"

    fun buildTimelineRoute(reportId: String) = "ciudadano_timeline/$reportId"
    fun buildCierreRoute(reportId: String) = "ciudadano_cierre/$reportId"
    fun buildGestorAdmitirRoute(reportId: String) = "gestor_admitir/$reportId"
    fun buildOperadorDetalleRoute(reportId: String) = "operador_detalle/$reportId"
    fun buildOperadorAvanceRoute(reportId: String) = "operador_actualizar_avance/$reportId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val onNavigateToBottomDestination: (BottomBarDestination) -> Unit = { destination ->
        if (navController.currentDestination?.route != destination.route) {
            navController.navigate(destination.route) {
                popUpTo(Routes.CIUDADANO_HOME) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // --- AUTH ---
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegistro = {
                    navController.navigate(Routes.REGISTRO)
                },
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        Role.CIUDADANO -> Routes.CIUDADANO_HOME
                        Role.GESTOR -> Routes.GESTOR_BANDEJA
                        Role.OPERADOR -> Routes.OPERADOR_REPORTES
                        Role.ADMINISTRADOR -> Routes.ADMIN_DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(
                authViewModel = authViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // --- ROL CIUDADANO (con BottomNavigationBar) ---
        composable(Routes.CIUDADANO_HOME) {
            HomeCiudadanoScreen(
                currentRoute = Routes.CIUDADANO_HOME,
                onNavigateToDestination = onNavigateToBottomDestination,
                onNavigateToCrearReporte = { navController.navigate(Routes.CIUDADANO_CREAR) },
                onNavigateToVerIncidentes = { navController.navigate(Routes.CIUDADANO_INCIDENTES) },
                onNavigateToHistorial = { navController.navigate(Routes.CIUDADANO_HISTORIAL) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CIUDADANO_NOTIFICACIONES) {
            NotificacionesScreen(
                currentRoute = Routes.CIUDADANO_NOTIFICACIONES,
                onNavigateToDestination = onNavigateToBottomDestination
            )
        }

        composable(Routes.CIUDADANO_PERFIL) {
            PerfilScreen(
                authViewModel = authViewModel,
                currentRoute = Routes.CIUDADANO_PERFIL,
                onNavigateToDestination = onNavigateToBottomDestination,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CIUDADANO_CREAR) {
            CrearReporteScreen(
                onBack = { navController.popBackStack() },
                onReporteCreado = {
                    navController.popBackStack()
                    navController.navigate(Routes.CIUDADANO_HISTORIAL)
                }
            )
        }

        composable(Routes.CIUDADANO_INCIDENTES) {
            VerIncidentesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTimeline = { reportId ->
                    navController.navigate(Routes.buildTimelineRoute(reportId))
                }
            )
        }

        composable(Routes.CIUDADANO_HISTORIAL) {
            HistorialRegistrosScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTimeline = { reportId ->
                    navController.navigate(Routes.buildTimelineRoute(reportId))
                },
                onNavigateToCierre = { reportId ->
                    navController.navigate(Routes.buildCierreRoute(reportId))
                }
            )
        }

        composable(
            route = Routes.CIUDADANO_TIMELINE,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: "101"
            TimelineSeguimientoScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onNavigateToCierre = { id -> navController.navigate(Routes.buildCierreRoute(id)) }
            )
        }

        composable(
            route = Routes.CIUDADANO_CIERRE,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: "104"
            CierreIncidenciaScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onFinalizado = { navController.popBackStack() }
            )
        }

        // --- ROL GESTOR ---
        composable(Routes.GESTOR_BANDEJA) {
            BandejaEntradaScreen(
                onNavigateToAdmitir = { reportId ->
                    navController.navigate(Routes.buildGestorAdmitirRoute(reportId))
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.GESTOR_ADMITIR,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: "101"
            AdmitirAsignarScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onAdmitidoExitoso = { navController.popBackStack() }
            )
        }

        // --- ROL OPERADOR ---
        composable(Routes.OPERADOR_REPORTES) {
            MisReportesAsignadosScreen(
                onNavigateToDetalle = { reportId ->
                    navController.navigate(Routes.buildOperadorDetalleRoute(reportId))
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.OPERADOR_DETALLE,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: "102"
            DetalleReporteAsignadoScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onNavigateToActualizarAvance = { id ->
                    navController.navigate(Routes.buildOperadorAvanceRoute(id))
                },
                onFinalizadoOperador = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.OPERADOR_AVANCE,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: "102"
            ActualizarAvanceScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onGuardadoExitoso = { navController.popBackStack() }
            )
        }

        // --- ROL ADMINISTRADOR ---
        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onNavigateToIndicadores = { navController.navigate(Routes.ADMIN_INDICADORES) },
                onNavigateToMonitoreo = { navController.navigate(Routes.ADMIN_MONITOREO) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_INDICADORES) {
            IndicadoresScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_MONITOREO) {
            MonitoreoReportesActivosScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTimeline = { reportId ->
                    navController.navigate(Routes.buildTimelineRoute(reportId))
                }
            )
        }
    }
}
