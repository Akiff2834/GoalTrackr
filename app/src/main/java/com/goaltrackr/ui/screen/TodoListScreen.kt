package com.goaltrackr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goaltrackr.data.api.CreateUpdateTodoRequest
import com.goaltrackr.viewmodel.TodoViewModel
import com.goaltrackr.viewmodel.TodoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onBack: () -> Unit, // ✅ Hata burada eksikti
    viewModel: TodoViewModel = viewModel(factory = TodoViewModelFactory()),
    modifier: Modifier = Modifier
) {
    val todos by viewModel.todos.collectAsState()
    val error by viewModel.error.collectAsState()

    var newTodoTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Public API Todo List") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (newTodoTitle.isNotBlank()) {
                        viewModel.createTodo(
                            CreateUpdateTodoRequest(
                                todo = newTodoTitle,
                                completed = false,
                                userId = 1
                            )
                        )
                        newTodoTitle = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newTodoTitle,
                onValueChange = { newTodoTitle = it },
                label = { Text("Yeni Todo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(text = "Hata: $error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn {
                items(todos) { todo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(todo.todo)
                                Text("Tamamlandı: ${if (todo.completed) "Evet" else "Hayır"}")
                            }
                            IconButton(onClick = {
                                viewModel.deleteTodo(todo.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Sil")
                            }
                        }
                    }
                }
            }
        }
    }

    // Ekran ilk açıldığında todo'ları çek
    LaunchedEffect(Unit) {
        viewModel.fetchTodos(userId = 1)
    }
}
