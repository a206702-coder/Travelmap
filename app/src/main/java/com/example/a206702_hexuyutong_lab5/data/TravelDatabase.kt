package com.example.travelmap

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Database — the single source of truth for persisted data.
 *
 * Exposes [travelDao] and is created through a thread-safe singleton so the whole
 * app shares one connection. A [seedCallback] inserts a few sample places the very
 * first time the database is created.
 */
@Database(entities = [TravelPlace::class], version = 1, exportSchema = false)
abstract class TravelDatabase : RoomDatabase() {

    abstract fun travelDao(): TravelDao

    companion object {
        @Volatile
        private var Instance: TravelDatabase? = null

        fun getDatabase(context: Context): TravelDatabase {
            // If the instance already exists, return it; otherwise create it inside
            // a synchronized block so only one instance is ever built.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TravelDatabase::class.java,
                    "travel_database"
                )
                    .addCallback(seedCallback)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

        /** Pre-populates the table with sample data on first creation only. */
        private val seedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                fun seed(name: String, address: String, date: String, desc: String) {
                    db.execSQL(
                        "INSERT INTO travel_places (name, address, date, description) " +
                            "VALUES (?, ?, ?, ?)",
                        arrayOf(name, address, date, desc)
                    )
                }

                seed(
                    "Sanya Yalong Bay",
                    "Yalong Bay Road, Jiyang District, Sanya, Hainan",
                    "2026.01.20 - 2026.01.25",
                    "No.1 Bay in the world, fine sand and clear sea water."
                )
                seed(
                    "Dali Ancient City",
                    "Dali City, Dali Bai Autonomous Prefecture, Yunnan",
                    "2026.02.05 - 2026.02.10",
                    "Capital of Nanzhao Kingdom, Ming and Qing architecture, rich ethnic customs."
                )
                seed(
                    "Xi'an Terracotta Army",
                    "Terracotta Army Scenic Area, Lintong District, Xi'an, Shaanxi",
                    "2026.03.10 - 2026.03.15",
                    "World Cultural Heritage, stunning underground army."
                )
            }
        }
    }
}
