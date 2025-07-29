package com.goaltrackr.data

import com.goaltrackr.data.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userGoalsCollection(): CollectionReference {
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("Kullanıcı oturumu yok")
        return firestore.collection("users").document(userId).collection("goals")
    }

    fun getGoalsFlow() = callbackFlow {
        val collection = try {
            userGoalsCollection()
        } catch (e: IllegalStateException) {
            close(e)
            return@callbackFlow
        }

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val goals = snapshot?.documents?.mapNotNull { it.toObject(Goal::class.java)?.copy(id = it.id) } ?: emptyList()
            trySend(goals)
        }

        awaitClose { listener.remove() }
    }

    suspend fun addGoal(goal: Goal) {
        val collection = userGoalsCollection()
        val doc = collection.document()
        val goalWithId = goal.copy(id = doc.id)
        doc.set(goalWithId).await()
    }

    suspend fun updateGoal(goal: Goal) {
        if (goal.id.isEmpty()) return
        val collection = userGoalsCollection()
        collection.document(goal.id).set(goal).await()
    }

    suspend fun deleteGoal(goalId: String) {
        val collection = userGoalsCollection()
        collection.document(goalId).delete().await()
    }

    suspend fun toggleGoalCompleted(goalId: String, completed: Boolean) {
        val collection = userGoalsCollection()
        collection.document(goalId).update("isCompleted", completed).await()
    }

    suspend fun deleteAllGoals() {
        val collection = userGoalsCollection()
        val snapshot = collection.get().await()
        snapshot.documents.forEach {
            it.reference.delete().await()
        }
    }
}
