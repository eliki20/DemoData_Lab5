package com.example.demodata.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demodata.ui.viewmodel.SessionViewModel

private sealed class ProfileViewState {
    object Menu : ProfileViewState()
    object MyProfile : ProfileViewState()
    object MyActivity : ProfileViewState()
}

data class OpcionPerfil(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

@Composable
fun ProfileScreen(
    sessionViewModel: SessionViewModel
) {
    var viewState by remember { mutableStateOf<ProfileViewState>(ProfileViewState.Menu) }
    val username by sessionViewModel.username.collectAsStateWithLifecycle()

    when (viewState) {
        ProfileViewState.Menu -> ProfileMenu(
            username = username ?: "Estudiante",
            onNavigateToProfile = { viewState = ProfileViewState.MyProfile },
            onNavigateToActivity = { viewState = ProfileViewState.MyActivity },
            onLogoutClick = { sessionViewModel.logout() }
        )

        ProfileViewState.MyProfile -> MyProfileScreen(
            sessionViewModel = sessionViewModel,
            username = username ?: "N/A",
            onBack = { viewState = ProfileViewState.Menu }
        )

        ProfileViewState.MyActivity -> MyActivityScreen(
            onBack = { viewState = ProfileViewState.Menu }
        )
    }
}

@Composable
private fun ProfileMenu(
    username: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val opciones = remember {
        listOf(
            OpcionPerfil(
                1,
                "Mis datos",
                "Información del estudiante y dispositivo",
                Icons.Default.Person
            ),
            OpcionPerfil(
                2,
                "Historial de Actividad",
                "Registros consolidados del sistema",
                Icons.Default.Receipt
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "DemoData — Lab 5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        opciones.forEach { opcion ->
            Card(
                onClick = {
                    if (opcion.id == 1) {
                        onNavigateToProfile()
                    } else {
                        onNavigateToActivity()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(opcion.titulo, fontWeight = FontWeight.SemiBold)
                    },
                    supportingContent = {
                        Text(opcion.descripcion)
                    },
                    leadingContent = {
                        Icon(
                            opcion.icono,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { mostrarDialogo = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Cerrar sesión")
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Confirmar cierre de sesión?") },
            text = { Text("Tus preferencias visuales del dispositivo se conservarán.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        onLogoutClick()
                    }
                ) {
                    Text(
                        "Sí, cerrar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MyProfileScreen(
    sessionViewModel: SessionViewModel,
    username: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDarkModePref by sessionViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDark = isDarkModePref ?: isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }

            Text(
                text = "Mis Datos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        ProfileMetadataItem("Nombre de Usuario", username)
        ProfileMetadataItem("Rol de Acceso", "Estudiante / Evaluador")
        ProfileMetadataItem("Directorio Local Interno", context.filesDir.absolutePath)
        ProfileMetadataItem("Fabricante del Equipo", Build.MANUFACTURER.uppercase())
        ProfileMetadataItem("Modelo del Dispositivo", Build.MODEL)
        ProfileMetadataItem(
            "Versión de Android",
            "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            ListItem(
                headlineContent = {
                    Text("Modo oscuro", fontWeight = FontWeight.SemiBold)
                },
                supportingContent = {
                    Text("Forzar aspecto visual nocturno")
                },
                trailingContent = {
                    Switch(
                        checked = isDark,
                        onCheckedChange = {
                            sessionViewModel.setDarkMode(it)
                        }
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        }
    }
}

@Composable
private fun MyActivityScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }

            Text(
                text = "Historial de Actividad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay archivos multimedia registrados en este ciclo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileMetadataItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}