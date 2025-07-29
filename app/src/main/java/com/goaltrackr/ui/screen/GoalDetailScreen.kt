@file:OptIn(ExperimentalMaterial3Api::class)

package com.goaltrackr.ui.screens

import com.goaltrackr.util.scheduleGoalReminder
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goaltrackr.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalDetailScreen(
    goalId: String,
    onBack: () -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val goals by viewModel.goals.collectAsState()
    val goal = goals.find { it.id == goalId }
    var isEditing by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(goal?.title.orEmpty()) }
    var description by remember { mutableStateOf(goal?.description.orEmpty()) }
    var category by remember { mutableStateOf(goal?.category.orEmpty()) }
    var isCompleted by remember { mutableStateOf(goal?.isCompleted ?: false) }
    var dueDate by remember { mutableStateOf(goal?.dueDate ?: 0L) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        if (dueDate > 0L) timeInMillis = dueDate
    }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hedef Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    if (!isEditing && goal != null) {
                        IconButton(onClick = {
                            val shareText = """
                                🎯 Hedef: ${goal.title}
                                📝 Açıklama: ${goal.description ?: "Yok"}
                                📂 Kategori: ${goal.category}
                                📅 Bitiş: ${if (goal.dueDate > 0L) dateFormat.format(Date(goal.dueDate)) else "Belirtilmedi"}
                                📌 Durum: ${if (goal.isCompleted) "Tamamlandı ✅" else "Devam Ediyor ⏳"}
                            """.trimIndent()

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "GoalTrackr Hedef Paylaşımı")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Hedefi Paylaş"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Paylaş")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (goal == null) {
                Text("Hedef bulunamadı")
                return@Column
            }

            if (!isEditing) {
                Text("Başlık: ${goal.title}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Açıklama: ${goal.description ?: "Yok"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kategori: ${goal.category}")
                Spacer(modifier = Modifier.height(8.dp))
                if (goal.dueDate > 0L) {
                    val formattedDate = dateFormat.format(Date(goal.dueDate))
                    Text("Bitiş Tarihi: $formattedDate")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Durum: ${if (goal.isCompleted) "Tamamlandı ✅" else "Devam Ediyor ⏳"}")
                Spacer(modifier = Modifier.height(16.dp))

                val progress = if (goal.isCompleted) 1f else 0f
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = progress,
                        strokeWidth = 8.dp,
                        color = if (goal.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (goal.isCompleted) "100%" else "0%",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { isEditing = true }) {
                    Text("Düzenle")
                }

            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                    Text("Tamamlandı")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, day: Int ->
                            calendar.set(year, month, day)
                            dueDate = calendar.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(
                        if (dueDate > 0L)
                            "Tarih: ${dateFormat.format(Date(dueDate))}"
                        else
                            "Tarih Seç"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        viewModel.updateGoal(
                            goal.copy(
                                title = title,
                                description = description,
                                category = category,
                                isCompleted = isCompleted,
                                dueDate = dueDate
                            ),
                            context = context
                        )
                        scheduleGoalReminder(context, goal.id, title, dueDate) // ✅ Hatırlatıcı çalıştır
                        isEditing = false
                    }) {
                        Text("Kaydet")
                    }

                    OutlinedButton(onClick = {
                        isEditing = false
                        title = goal.title
                        description = goal.description ?: ""
                        category = goal.category
                        isCompleted = goal.isCompleted
                        dueDate = goal.dueDate
                    }) {
                        Text("İptal")
                    }
                }
            }
        }
    }
}
