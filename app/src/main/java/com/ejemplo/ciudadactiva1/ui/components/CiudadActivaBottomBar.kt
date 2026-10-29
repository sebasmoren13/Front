package com.ejemplo.ciudadactiva1.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    INICIO("ciudadano_home", "Inicio", Icons.Default.Home),
    NOTIFICACIONES("ciudadano_notificaciones", "Notificaciones", Icons.Default.Notifications),
    PERFIL("ciudadano_perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun CiudadActivaBottomBar(
    currentRoute: String,
    onNavigateToDestination: (BottomBarDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigateToDestination(destination) },
                icon = { Icon(destination.icon, contentDescription = destination.title) },
                label = { Text(destination.title) }
            )
        }
    }
}
