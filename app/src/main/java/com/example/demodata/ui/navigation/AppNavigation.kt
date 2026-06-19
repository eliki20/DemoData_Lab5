package com.example.demodata.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demodata.ui.screens.AudioScreen
import com.example.demodata.ui.screens.GpsScreen
import com.example.demodata.ui.screens.MediaScreen
import com.example.demodata.ui.screens.NotificationsScreen
import com.example.demodata.ui.screens.ProfileScreen
import com.example.demodata.ui.screens.SyncScreen
import com.example.demodata.ui.viewmodel.GpsViewModel
import com.example.demodata.ui.viewmodel.SessionViewModel

sealed class RutaLab5(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Gps : RutaLab5("gps", "GNSS", Icons.Default.LocationOn)
    object Media : RutaLab5("media", "Multimedia", Icons.Default.CameraAlt)
    object Audio : RutaLab5("audio", "Audio", Icons.Default.Mic)
    object Sync : RutaLab5("sync", "Sync", Icons.Default.CloudSync)
    object Notif : RutaLab5("notif", "Notif", Icons.Default.Notifications)
    object Profile : RutaLab5("profile", "Perfil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    gpsViewModel: GpsViewModel,
    sessionViewModel: SessionViewModel
) {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        RutaLab5.Gps,
        RutaLab5.Media,
        RutaLab5.Audio,
        RutaLab5.Sync,
        RutaLab5.Notif,
        RutaLab5.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("DemoData")
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(tab.label)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = RutaLab5.Gps.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(RutaLab5.Gps.route) {
                GpsScreen(gpsViewModel = gpsViewModel)
            }

            composable(RutaLab5.Media.route) {
                MediaScreen()
            }

            composable(RutaLab5.Audio.route) {
                AudioScreen()
            }

            composable(RutaLab5.Sync.route) {
                SyncScreen()
            }

            composable(RutaLab5.Notif.route) {
                NotificationsScreen()
            }

            composable(RutaLab5.Profile.route) {
                ProfileScreen(
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
}