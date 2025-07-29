// app/src/main/java/com/goaltrackr/data/local/GoalDao.kt
package com.goaltrackr.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.goaltrackr.data.model.Goal
import kotlinx.coroutines.flow.Flow

// Room DAO (Data Access Object) arayüzü.
// Yerel veritabanı (goals tablosu) ile etkileşim kurmak için metodları tanımlar.
@Dao
interface GoalDao {

    // Tüm hedefleri getir (Flow ile gerçek zamanlı güncellemeler)
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY dueDate ASC")
    fun getAllGoals(userId: String): Flow<List<Goal>>

    // Belirli bir ID'ye sahip hedefi getir
    @Query("SELECT * FROM goals WHERE id = :goalId AND userId = :userId")
    suspend fun getGoalById(goalId: Int, userId: String): Goal?

    // Yeni bir hedef ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Çakışma durumunda değiştir
    suspend fun insertGoal(goal: Goal)

    // Bir hedefi güncelle
    @Update
    suspend fun updateGoal(goal: Goal)

    // Bir hedefi sil
    @Delete
    suspend fun deleteGoal(goal: Goal)

    // Tüm hedefleri sil
    @Query("DELETE FROM goals WHERE userId = :userId")
    suspend fun deleteAllGoals(userId: String)
}
