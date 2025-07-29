@file:OptIn(ExperimentalMaterial3Api::class)

package com.goaltrackr.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goaltrackr.viewmodel.GoalViewModel
import com.goaltrackr.data.model.Goal
import com.goaltrackr.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController




@Composable
fun GoalListScreen(
    viewModel: GoalViewModel = viewModel(),
    onLogout: () -> Unit,
    onGoalClick: (String) -> Unit,
    navController: NavController,

    onDeleteAllGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.filteredGoals.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Genel") }
    var isCompleted by remember { mutableStateOf(false) }
    var dueDateMillis by remember { mutableStateOf(0L) }
    var dueTimeMillis by remember { mutableStateOf(0L) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val categories = remember { mutableStateListOf<String>() }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryText by remember { mutableStateOf("Tümü") }

    LaunchedEffect(viewModel.goals) {
        categories.clear()
        categories.add("Tümü")
        categories.addAll(viewModel.getAvailableCategories().filter { it.isNotBlank() })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GoalTrackr") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Hedeflerim", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = selectedFilter == GoalViewModel.GoalFilter.ALL,
                    onClick = { viewModel.setFilter(GoalViewModel.GoalFilter.ALL) },
                    label = { Text("Tümü") }
                )
                FilterChip(
                    selected = selectedFilter == GoalViewModel.GoalFilter.COMPLETED,
                    onClick = { viewModel.setFilter(GoalViewModel.GoalFilter.COMPLETED) },
                    label = { Text("Tamamlanan") }
                )
                FilterChip(
                    selected = selectedFilter == GoalViewModel.GoalFilter.INCOMPLETE,
                    onClick = { viewModel.setFilter(GoalViewModel.GoalFilter.INCOMPLETE) },
                    label = { Text("Tamamlanmamış") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoryText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategoriye göre filtrele") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategoryText = cat
                                viewModel.setCategoryFilter(if (cat == "Tümü") null else cat)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(goals, key = { it.id }) { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onGoalClick(goal.id) },
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(goal.title, style = MaterialTheme.typography.titleMedium)
                                    goal.description?.let {
                                        Text(it, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text("Kategori: ${goal.category}", style = MaterialTheme.typography.bodySmall)
                                    if (goal.dueDate > 0L) {
                                        val date = dateFormat.format(Date(goal.dueDate))
                                        val time = timeFormat.format(Date(goal.dueDate))
                                        Text("Bitiş: $date $time", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    IconButton(onClick = {
                                        viewModel.toggleCompleted(goal.id, !goal.isCompleted)
                                    }) {
                                        Icon(
                                            imageVector = if (goal.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Tamamlandı",
                                            tint = if (goal.isCompleted) Color(0xFF4CAF50) else Color.Gray
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteGoal(goal.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDeleteAllGoals,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tüm Hedefleri Sil", color = MaterialTheme.colorScheme.onError)
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val fullDateTime = Calendar.getInstance().apply {
                                timeInMillis = dueDateMillis
                                val hourMin = Calendar.getInstance().apply { timeInMillis = dueTimeMillis }
                                set(Calendar.HOUR_OF_DAY, hourMin.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, hourMin.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                            }.timeInMillis

                            val newGoal = Goal(
                                title = title,
                                description = description,
                                category = category,
                                isCompleted = isCompleted,
                                dueDate = fullDateTime,
                                userId = userId
                            )

                            viewModel.addGoal(newGoal)
                            if (fullDateTime > 0L) {
                                viewModel.scheduleReminder(context, newGoal)
                            }

                            title = ""
                            description = ""
                            category = "Genel"
                            isCompleted = false
                            dueDateMillis = 0L
                            dueTimeMillis = 0L
                            showDialog = false
                        }) {
                            Text("Ekle")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("İptal")
                        }
                    },
                    title = { Text("Yeni Hedef") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Başlık") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Açıklama") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Kategori") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                                Text("Tamamlandı")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                val now = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, y: Int, m: Int, d: Int ->
                                        calendar.set(y, m, d)
                                        dueDateMillis = calendar.timeInMillis
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Text(
                                    if (dueDateMillis > 0L)
                                        "Tarih: ${dateFormat.format(Date(dueDateMillis))}"
                                    else "Tarih Seç"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                val now = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val timeCal = Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, hour)
                                            set(Calendar.MINUTE, minute)
                                        }
                                        dueTimeMillis = timeCal.timeInMillis
                                    },
                                    now.get(Calendar.HOUR_OF_DAY),
                                    now.get(Calendar.MINUTE),
                                    true
                                ).show()
                            }) {
                                Text(
                                    if (dueTimeMillis > 0L)
                                        "Saat: ${timeFormat.format(Date(dueTimeMillis))}"
                                    else "Saat Seç"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
