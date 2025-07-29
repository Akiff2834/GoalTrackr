package com.goaltrackr.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goaltrackr.data.FirestoreRepository
import com.goaltrackr.data.model.Goal
import com.goaltrackr.receiver.ReminderReceiver
import com.goaltrackr.util.AnalyticsHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GoalViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    enum class GoalFilter {
        ALL, COMPLETED, INCOMPLETE
    }

    private val _selectedFilter = MutableStateFlow(GoalFilter.ALL)
    val selectedFilter: StateFlow<GoalFilter> = _selectedFilter

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val filteredGoals: StateFlow<List<Goal>> = combine(_goals, _selectedFilter, _selectedCategory) { goals, filter, category ->
        goals
            .filter {
                when (filter) {
                    GoalFilter.ALL -> true
                    GoalFilter.COMPLETED -> it.isCompleted
                    GoalFilter.INCOMPLETE -> !it.isCompleted
                }
            }
            .filter {
                category == null || it.category.equals(category, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: GoalFilter) {
        _selectedFilter.value = filter
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun getAvailableCategories(): List<String> {
        return _goals.value.mapNotNull { it.category }.distinct()
    }

    init {
        if (FirebaseAuth.getInstance().currentUser != null) {
            observeGoals()
            migrateCompletedFields()
        }
    }

    private fun observeGoals() {
        viewModelScope.launch {
            repository.getGoalsFlow()
                .catch { e -> e.printStackTrace() }
                .collect { goalList ->
                    _goals.value = goalList
                }
        }
    }

    fun addGoal(
        title: String,
        description: String,
        category: String,
        isCompleted: Boolean,
        dueDate: Long
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val newGoal = Goal(
            title = title,
            description = description,
            category = category,
            isCompleted = isCompleted,
            dueDate = dueDate,
            userId = userId
        )

        viewModelScope.launch {
            repository.addGoal(newGoal)
            AnalyticsHelper.logGoalCreated(title, category)
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.addGoal(goal)
            AnalyticsHelper.logGoalCreated(goal.title, goal.category ?: "Bilinmiyor")
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            val goalToDelete = _goals.value.find { it.id == goalId }
            if (goalToDelete != null) {
                repository.deleteGoal(goalId)
                AnalyticsHelper.logGoalDeleted(goalToDelete.title, goalToDelete.category ?: "Bilinmiyor")
            }
        }
    }

    fun toggleCompleted(goalId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleGoalCompleted(goalId, completed)
        }
    }

    fun updateGoal(goal: Goal, context: Context? = null) {
        viewModelScope.launch {
            repository.updateGoal(goal)
            AnalyticsHelper.logGoalUpdated(goal.title, goal.category ?: "Bilinmiyor")
            if (context != null && goal.dueDate > 0L) {
                scheduleReminder(context, goal)
            }
        }
    }

    fun deleteAllGoals() {
        viewModelScope.launch {
            repository.deleteAllGoals()
        }
    }

    private fun migrateCompletedFields() {
        viewModelScope.launch {
            repository.getGoalsFlow()
                .take(1)
                .collect { goals ->
                    goals.forEach { goal ->
                        if (goal.id.isNotEmpty()) {
                            repository.toggleGoalCompleted(goal.id, goal.isCompleted)
                        }
                    }
                }
        }
    }

    fun scheduleReminder(context: Context, goal: Goal) {
        if (goal.dueDate <= 0L) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", goal.title)
            putExtra(
                "dueDate",
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(goal.dueDate))
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goal.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            goal.dueDate,
            pendingIntent
        )
    }
}
