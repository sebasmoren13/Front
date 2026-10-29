package com.ejemplo.ciudadactiva1.ui.screens.ciudadano

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.ui.components.BottomBarDestination
import com.ejemplo.ciudadactiva1.ui.components.CiudadActivaBottomBar
import com.ejemplo.ciudadactiva1.ui.theme.ForestGreen

@Composable
fun HomeCiudadanoScreen(
    currentRoute: String = BottomBarDestination.INICIO.route,
    onNavigateToDestination: (BottomBarDestination) -> Unit,
    onNavigateToCrearReporte: () -> Unit,
    onNavigateToVerIncidentes: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by SessionManager.currentUser.collectAsState()
    val userName = currentUser?.name?.ifBlank { "Usuario" } ?: "Usuario"
    val userInitials = currentUser?.avatarInitials ?: "U"

    Scaffold(
        bottomBar = {
            CiudadActivaBottomBar(
                currentRoute = currentRoute,
                onNavigateToDestination = onNavigateToDestination
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Task 3: Header dinámico (sin "Hola Ana" ni "AG" hardcodeados)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¡Hola, $userName!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Bienvenido a CiudadActiva",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Cerrar sesión",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Accesos Directos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            DirectAccessCard(
                title = "1. Registrar Incidente",
                subtitle = "Reporta fallas viales, alumbrado o residuos",
                icon = Icons.Default.Add,
                backgroundColor = Color(0xFFE8F8F5),
                iconColor = ForestGreen,
                onClick = onNavigateToCrearReporte
            )

            Spacer(modifier = Modifier.height(14.dp))

            DirectAccessCard(
                title = "2. Ver Incidentes",
                subtitle = "Explora el mapa y lista de reportes de tu ciudad",
                icon = Icons.Default.Map,
                backgroundColor = Color(0xFFEBF5FB),
                iconColor = Color(0xFF2980B9),
                onClick = onNavigateToVerIncidentes
            )

            Spacer(modifier = Modifier.height(14.dp))

            DirectAccessCard(
                title = "3. Ver Historial de Registro",
                subtitle = "Consulta el estado y seguimiento de tus reportes",
                icon = Icons.Default.History,
                backgroundColor = Color(0xFFFEF9E7),
                iconColor = Color(0xFFD35400),
                onClick = onNavigateToHistorial
            )
        }
    }
}

@Composable
fun DirectAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
