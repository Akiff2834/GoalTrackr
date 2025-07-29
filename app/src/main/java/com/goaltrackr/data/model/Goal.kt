package com.goaltrackr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.UUID

@Entity(tableName = "goals")
@IgnoreExtraProperties
data class Goal(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val title: String = "",
    val description: String? = null,
    val category: String = "",
    val isCompleted: Boolean = false,
    val dueDate: Long = 0L,

    // Firestore'da doldurulacak ama Room'da tabloya dahil edilmesin:
    @get:Exclude
    val userId: String = ""
)
