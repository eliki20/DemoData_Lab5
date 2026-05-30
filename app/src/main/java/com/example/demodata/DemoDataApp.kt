package com.example.demodata

import android.app.Application
import androidx.room.Room
import com.example.demodata.data.local.AppDatabase
import com.example.demodata.data.repository.GpsRepository

class DemoDataApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "demo_data_db"
        ).build()
    }

    val gpsRepository: GpsRepository by lazy {
        GpsRepository(
            database.gpsGoogleDao(),
            database.gpsSensorsDao()
        )
    }
}