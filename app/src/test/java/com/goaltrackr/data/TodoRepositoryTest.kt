package com.goaltrackr.data.repository

import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.data.api.TodoApiService
import com.goaltrackr.data.api.TodoDto
import com.goaltrackr.util.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class TodoRepositoryTest {private lateinit var api: TodoApiService
    private lateinit var repository: TodoRepository

    @Before
    fun setup() {
        api = mockk()
        repository = TodoRepository(api)
    }

    @Test
    fun `getTodos should return error when response is not successful`() = runBlocking {
        // Arrange
        coEvery { api.getTodos(any(), any()) } returns Response.error(
            404,
            ResponseBody.create(null, "Not found")
        )

        // Act
        val result = repository.getTodos(userId = 1, token = "token")

        // Assert
        assert(result is Result.Error)
        assertEquals("API Hatası: 404", (result as Result.Error).message)
    }

    @Test
    fun `createTodo should return error when response is not successful`() = runBlocking {
        coEvery { api.createTodo(any(), any()) } returns Response.error(
            400,
            ResponseBody.create(null, "Bad request")
        )

        val request = CreateUpdateTodoRequest("title", false, 1)

        val result = repository.createTodo(request, token = "token")

        assert(result is Result.Error)
        assertEquals("API Hatası: 400", (result as Result.Error).message)
    }

    @Test
    fun `updateTodo should return error when response is not successful`() = runBlocking {
        coEvery { api.updateTodo(any(), any(), any()) } returns Response.error(
            403,
            ResponseBody.create(null, "Forbidden")
        )

        val request = CreateUpdateTodoRequest("updated", true, 1)

        val result = repository.updateTodo(id = 1, request, token = "token")

        assert(result is Result.Error)
        assertEquals("API Hatası: 403", (result as Result.Error).message)
    }

    @Test
    fun `deleteTodo should return error when response is not successful`() = runBlocking {
        coEvery { api.deleteTodo(any(), any()) } returns Response.error(
            500,
            ResponseBody.create(null, "Server error")
        )

        val result = repository.deleteTodo(id = 1, token = "token")

        assert(result is Result.Error)
        assertEquals("API Hatası: 500", (result as Result.Error).message)
    }

    @Test
    fun `getTodos should return error on exception`() = runBlocking {
        coEvery { api.getTodos(any(), any()) } throws RuntimeException("Bağlantı hatası")

        val result = repository.getTodos(userId = 1, token = "token")

        assert(result is Result.Error)
        assert((result as Result.Error).message!!.contains("Bağlantı hatası"))
    }
}