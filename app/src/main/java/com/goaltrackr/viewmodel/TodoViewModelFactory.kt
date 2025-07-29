package com.goaltrackr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goaltrackr.data.api.RetrofitInstance
import com.goaltrackr.data.repository.TodoRepository

class TodoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = TodoRepository(RetrofitInstance.api)
        return TodoViewModel(repository) as T
    }
}
