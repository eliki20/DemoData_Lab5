package com.example.demodata.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demodata.data.local.dao.GpsGoogleDao
import com.example.demodata.data.local.dao.GpsSensorsDao
import com.example.demodata.data.local.entity.GpsGoogleEntity
import com.example.demodata.data.local.entity.GpsSensorsEntity

@Database(
    entities = [
        GpsGoogleEntity::class,
        GpsSensorsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gpsGoogleDao(): GpsGoogleDao

    abstract fun gpsSensorsDao(): GpsSensorsDao
}