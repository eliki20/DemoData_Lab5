package com.example.demodata.ui.screens

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demodata.ui.viewmodel.SessionViewModel

private sealed class ProfileViewState {
    object Menu : ProfileViewState()
    object MyProfile : ProfileViewState()
    object MyActivity : ProfileViewState()
}

@Composable
fun ProfileScreen(
    sessionViewModel: SessionViewModel,
    username: String? = "Usuario"
) {
    var viewState by remember { mutableStateOf<ProfileViewState>(ProfileViewState.Menu) }

    when (viewState) {
        ProfileViewState.Menu -> ProfileMenu(
            username = username,
            onNavigateToProfile = { viewState = ProfileViewState.MyProfile },
            onNavigateToActivity = { viewState = ProfileViewState.MyActivity }
        )

        ProfileViewState.MyProfile -> MyProfileScreen(
            sessionViewModel = sessionViewModel,
            username = username,
            onBack = { viewState = ProfileViewState.Menu }
        )

        ProfileViewState.MyActivity -> MyActivityScreen(
            onBack = { viewState = ProfileViewState.Menu }
        )
    }
}

@Composable
private fun ProfileMenu(
    username: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = username ?: "Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToProfile
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mi Perfil")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToActivity
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mi Actividad")
            }
        }
    }
}

@Composable
private fun MyProfileScreen(
    sessionViewModel: SessionViewModel,
    username: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDarkModePref by sessionViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDark = isDarkModePref ?: isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack) {
            Text("Volver")
        }

        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        ProfileMetadataItem("Username", username ?: "N/A")
        ProfileMetadataItem("Rol", "Administrador / Operador")
        ProfileMetadataItem("Directorio Local", context.filesDir.absolutePath)
        ProfileMetadataItem("Dispositivo", "${Build.MANUFACTURER} ${Build.MODEL}")
        ProfileMetadataItem("Android Version", Build.VERSION.RELEASE)
        ProfileMetadataItem("API Level", Build.VERSION.SDK_INT.toString())

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Modo noche")
                }

                Switch(
                    checked = isDark,
                    onCheckedChange = {
                        sessionViewModel.setDarkMode(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                sessionViewModel.logout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cerrar sesión")
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
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mi Actividad",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Aquí se mostrará el historial de actividad del usuario.")
    }
}

@Composable
private fun ProfileMetadataItem(
    label: String,
    value: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}