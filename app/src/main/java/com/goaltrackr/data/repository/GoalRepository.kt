package com.goaltrackr.data.repository

import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.data.api.TodoApiService
import com.goaltrackr.data.local.GoalDao
import com.goaltrackr.data.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val todoApiService: TodoApiService
) {

    fun getAllGoalsLocal(userId: String): Flow<List<Goal>> {
        return goalDao.getAllGoals(userId)
    }

    suspend fun insertGoalLocal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoalLocal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoalLocal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun deleteAllGoalsLocal(userId: String) {
        goalDao.deleteAllGoals(userId)
    }

    suspend fun getTodosRemote(userId: Int, authToken: String? = null): List<Goal> {
        val response = todoApiService.getTodos(userId, authToken)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.todos.map { todoDto ->
                Goal(
                    id = todoDto.id.toString(),
                    title = todoDto.todo,
                    description = null,
                    category = "API Todo",
                    dueDate = System.currentTimeMillis(),
                    isCompleted = todoDto.completed,
                    userId = todoDto.userId.toString()
                )
            }
        } else {
            throw Exception("API'den todolar alınamadı: ${response.code()} ${response.message()}")
        }
    }

    suspend fun createTodoRemote(
        todo: String,
        completed: Boolean,
        userId: Int,
        authToken: String? = null
    ): Goal {
        val request = CreateUpdateTodoRequest(todo = todo, completed = completed, userId = userId)
        val response = todoApiService.createTodo(request, authToken)
        if (response.isSuccessful && response.body() != null) {
            val todoDto = response.body()!!
            return Goal(
                id = todoDto.id.toString(),
                title = todoDto.todo,
                description = null,
                category = "API Todo",
                dueDate = System.currentTimeMillis(),
                isCompleted = todoDto.completed,
                userId = todoDto.userId.toString()
            )
        } else {
            throw Exception("API'ye todo oluşturulamadı: ${response.code()} ${response.message()}")
        }
    }

    suspend fun updateTodoRemote(
        id: Int,
        todo: String,
        completed: Boolean,
        authToken: String? = null
    ): Goal {
        val request = CreateUpdateTodoRequest(todo = todo, completed = completed)
        val response = todoApiService.updateTodo(id, request, authToken)
        if (response.isSuccessful && response.body() != null) {
            val todoDto = response.body()!!
            return Goal(
                id = todoDto.id.toString(),
                title = todoDto.todo,
                description = null,
                category = "API Todo",
                dueDate = System.currentTimeMillis(),
                isCompleted = todoDto.completed,
                userId = todoDto.userId.toString()
            )
        } else {
            throw Exception("API'de todo güncellenemedi: ${response.code()} ${response.message()}")
        }
    }

    suspend fun deleteTodoRemote(id: Int, authToken: String? = null) {
        val response = todoApiService.deleteTodo(id, authToken)
        if (!response.isSuccessful) {
            throw Exception("API'den todo silinemedi: ${response.code()} ${response.message()}")
        }
    }
}
