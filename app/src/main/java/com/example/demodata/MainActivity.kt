package com.example.demodata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demodata.ui.screens.GpsScreen
import com.example.demodata.ui.theme.AppTheme
import com.example.demodata.ui.viewmodel.GpsViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demodata.data.session.SessionManager
import com.example.demodata.ui.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val app = application as DemoDataApp

            val gpsViewModel: GpsViewModel = viewModel(
                factory = GpsViewModel.Factory(app.gpsRepository)
            )

            val sessionViewModel: SessionViewModel = viewModel(
                factory = SessionViewModel.Factory(
                    SessionManager(applicationContext)
                )
            )

            val darkModePref by sessionViewModel.isDarkMode
                .collectAsStateWithLifecycle()

            val darkTheme = darkModePref ?: false

            AppTheme(
                darkTheme = darkTheme
            ) {
                GpsScreen(gpsViewModel = gpsViewModel)
            }

        }
    }
}