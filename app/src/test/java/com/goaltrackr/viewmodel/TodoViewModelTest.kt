package com.goaltrackr.viewmodel

import app.cash.turbine.test
import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.data.api.TodoDto
import com.goaltrackr.data.repository.TodoRepository
import com.goaltrackr.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest


@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private lateinit var repository: TodoRepository
    private lateinit var viewModel: TodoViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        viewModel = TodoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchTodos success updates todos`() = runTest {
        val fakeTodos = listOf(TodoDto(id = 1, todo = "Learn Compose", completed = false, userId = 1))
        coEvery { repository.getTodos(any(), any()) } returns Result.Success(fakeTodos)

        viewModel.fetchTodos()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.todos.test {
            assertEquals(fakeTodos, awaitItem())
        }
    }

    @Test
    fun `fetchTodos failure updates error`() = runTest {
        coEvery { repository.getTodos(any(), any()) } returns Result.Error("API Error")

        viewModel.fetchTodos()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.error.test {
            assertEquals("API Error", awaitItem())
        }
    }

    @Test
    fun `createTodo success calls fetchTodos`() = runTest {
        val request = CreateUpdateTodoRequest("New Task", false, 1)
        coEvery { repository.createTodo(request, any()) } returns Result.Success(TodoDto(1, "New Task", false, 1))
        coEvery { repository.getTodos(any(), any()) } returns Result.Success(emptyList())

        viewModel.createTodo(request)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.getTodos(any(), any()) }
    }

    @Test
    fun `createTodo failure sets error`() = runTest {
        val request = CreateUpdateTodoRequest("New Task", false, 1)
        coEvery { repository.createTodo(request, any()) } returns Result.Error("Create failed")

        viewModel.createTodo(request)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.error.test {
            assertEquals("Create failed", awaitItem())
        }
    }

    @Test
    fun `updateTodo success calls fetchTodos`() = runTest {
        val request = CreateUpdateTodoRequest("Updated Task", true, 1)
        coEvery { repository.updateTodo(1, request, any()) } returns Result.Success(TodoDto(1, "Updated Task", true, 1))
        coEvery { repository.getTodos(any(), any()) } returns Result.Success(emptyList())

        viewModel.updateTodo(1, request)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.getTodos(any(), any()) }
    }

    @Test
    fun `updateTodo failure sets error`() = runTest {
        val request = CreateUpdateTodoRequest("Updated Task", true, 1)
        coEvery { repository.updateTodo(1, request, any()) } returns Result.Error("Update failed")

        viewModel.updateTodo(1, request)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.error.test {
            assertEquals("Update failed", awaitItem())
        }
    }

    @Test
    fun `deleteTodo success calls fetchTodos`() = runTest {
        coEvery { repository.deleteTodo(1, any()) } returns Result.Success(Unit)
        coEvery { repository.getTodos(any(), any()) } returns Result.Success(emptyList())

        viewModel.deleteTodo(1)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.getTodos(any(), any()) }
    }

    @Test
    fun `deleteTodo failure sets error`() = runTest {
        coEvery { repository.deleteTodo(1, any()) } returns Result.Error("Delete failed")

        viewModel.deleteTodo(1)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.error.test {
            assertEquals("Delete failed", awaitItem())
        }
    }
}
