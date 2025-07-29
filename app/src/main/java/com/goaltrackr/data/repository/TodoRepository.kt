package com.goaltrackr.data.repository

import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.data.api.TodoApiService
import com.goaltrackr.data.api.TodoDto
import com.goaltrackr.util.Result

class TodoRepository(private val api: TodoApiService) {

    suspend fun getTodos(userId: Int?, token: String?): Result<List<TodoDto>> {
        return try {
            val response = api.getTodos(userId, token)
            if (response.isSuccessful) {
                Result.Success(response.body()?.todos ?: emptyList())
            } else {
                Result.Error("API Hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("İstisna: ${e.localizedMessage}")
        }
    }

    suspend fun createTodo(request: CreateUpdateTodoRequest, token: String?): Result<TodoDto> {
        return try {
            val response = api.createTodo(request, token)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("API Hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("İstisna: ${e.localizedMessage}")
        }
    }

    suspend fun updateTodo(id: Int, request: CreateUpdateTodoRequest, token: String?): Result<TodoDto> {
        return try {
            val response = api.updateTodo(id, request, token)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("API Hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("İstisna: ${e.localizedMessage}")
        }
    }

    suspend fun deleteTodo(id: Int, token: String?): Result<Unit> {
        return try {
            val response = api.deleteTodo(id, token)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API Hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("İstisna: ${e.localizedMessage}")
        }
    }
}
