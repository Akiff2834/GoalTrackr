package com.goaltrackr.util

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.goaltrackr.worker.GoalReminderWorker
import java.util.concurrent.TimeUnit

fun scheduleGoalReminder(context: Context, goalId: String, title: String, dueDate: Long) {
    val delay = dueDate - System.currentTimeMillis()
    if (delay <= 0) return // geçmiş tarihleri atla

    val inputData = Data.Builder()
        .putString("GOAL_ID", goalId)
        .putString("GOAL_TITLE", title)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<GoalReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
