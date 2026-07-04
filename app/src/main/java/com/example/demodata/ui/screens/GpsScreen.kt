package com.example.demodata.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demodata.services.GpsCaptureService
import com.example.demodata.ui.viewmodel.ComparativeGpsRecord
import com.example.demodata.ui.viewmodel.GpsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GpsScreen(
    gpsViewModel: GpsViewModel
) {
    val context = LocalContext.current
    var capturando by remember { mutableStateOf(false) }

    val googlePoints by gpsViewModel.googlePoints.collectAsStateWithLifecycle()
    val sensorsPoints by gpsViewModel.sensorsPoints.collectAsStateWithLifecycle()
    val history by gpsViewModel.comparativeHistory.collectAsStateWithLifecycle()

    val permisos = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val estadoPermisos = rememberMultiplePermissionsState(permisos)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "GNSS Tracking",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Monitoreo de ubicación en tiempo real",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!estadoPermisos.allPermissionsGranted) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Se necesitan permisos de ubicación.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            estadoPermisos.launchMultiplePermissionRequest()
                        }
                    ) {
                        Text("Conceder permisos")
                    }
                }
            }
            return
        }

        Button(
            onClick = {
                val intent = Intent(context, GpsCaptureService::class.java)

                if (!capturando) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    capturando = true
                } else {
                    context.stopService(intent)
                    capturando = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (capturando)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (capturando) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                if (capturando)
                    "Detener captura"
                else
                    "Capturar coordenada (cada 10 segundos)"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CounterCard(
                title = "Google FLP",
                count = googlePoints.size,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            CounterCard(
                title = "Sensor GNSS",
                count = sensorsPoints.size,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Historial corporativo (sincronizado)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = history,
                key = { it.timestamp }
            ) { record ->
                ComparativeCaptureCard(record)
            }
        }
    }
}

@Composable
private fun CounterCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$count registros",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ComparativeCaptureCard(
    record: ComparativeGpsRecord
) {
    val dateFormat = remember {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "Instante: ${dateFormat.format(Date(record.timestamp))}",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("GOOGLE FLP", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                    if (record.google?.latitude == null) {
                        Text(
                            "SIN SEÑAL",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Lat: ${record.google.latitude}")
                        Text("Lon: ${record.google.longitude}")
                        Text("Acc: ±${record.google.accuracy} m")
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("SENSOR GNSS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)

                    if (record.sensors?.latitude == null) {
                        Text(
                            "SIN SEÑAL",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Lat: ${record.sensors.latitude}")
                        Text("Lon: ${record.sensors.longitude}")
                        Text("Alt: ${record.sensors.altitude ?: "N/A"}")
                    }
                }
            }
        }
    }
}