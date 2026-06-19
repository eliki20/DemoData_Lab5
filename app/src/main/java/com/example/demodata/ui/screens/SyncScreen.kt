package com.example.demodata.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demodata.DemoDataApp
import com.example.demodata.ui.viewmodel.SyncViewModel

@Composable
fun SyncScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as DemoDataApp

    val vm: SyncViewModel = viewModel(
        factory = SyncViewModel.Factory(
            app.gpsRepository,
            app.mediaRepository,
            app.audioRepository
        )
    )

    val counts by vm.counts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Sync Center",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Inventario de registros locales pendientes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Simulación de sincronización: ${counts.total} registros",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Sincronizar ahora")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total pendiente: ${counts.total}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        SyncCountCard(
            title = "GPS Google FLP",
            count = counts.gpsGoogle,
            icon = Icons.Default.LocationOn
        )

        SyncCountCard(
            title = "GPS Sensor GNSS",
            count = counts.gpsSensors,
            icon = Icons.Default.LocationOn
        )

        SyncCountCard(
            title = "Fotos",
            count = counts.photos,
            icon = Icons.Default.Image
        )

        SyncCountCard(
            title = "Videos",
            count = counts.videos,
            icon = Icons.Default.Videocam
        )

        SyncCountCard(
            title = "Audios",
            count = counts.audios,
            icon = Icons.Default.AudioFile
        )
    }
}

@Composable
private fun SyncCountCard(
    title: String,
    count: Int,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}