// app/src/main/java/com/goaltrackr/data/local/AppDatabase.kt
package com.goaltrackr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goaltrackr.data.model.Goal

// Room veritabanı sınıfı.
// Veritabanı versiyonunu, entity'leri ve DAO'ları tanımlar.
@Database(entities = [Goal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
}
