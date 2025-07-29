package com.goaltrackr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.data.api.TodoDto
import com.goaltrackr.data.repository.TodoRepository
import com.goaltrackr.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<TodoDto>>(emptyList())
    val todos: StateFlow<List<TodoDto>> = _todos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchTodos(userId: Int? = null, token: String? = null) {
        viewModelScope.launch {
            when (val result = repository.getTodos(userId, token)) {
                is Result.Success -> _todos.value = result.data
                is Result.Error -> _error.value = result.message
            }
        }
    }

    fun createTodo(
        request: CreateUpdateTodoRequest,
        token: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = repository.createTodo(request, token)) {
                is Result.Success -> {
                    fetchTodos()
                    onSuccess()
                }
                is Result.Error -> _error.value = result.message
            }
        }
    }

    fun updateTodo(
        id: Int,
        request: CreateUpdateTodoRequest,
        token: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = repository.updateTodo(id, request, token)) {
                is Result.Success -> {
                    fetchTodos()
                    onSuccess()
                }
                is Result.Error -> _error.value = result.message
            }
        }
    }

    fun deleteTodo(id: Int, token: String? = null) {
        viewModelScope.launch {
            when (val result = repository.deleteTodo(id, token)) {
                is Result.Success -> fetchTodos()
                is Result.Error -> _error.value = result.message
            }
        }
    }
}
