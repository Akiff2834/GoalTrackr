package com.goaltrackr.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goaltrackr.util.NotificationHelper

class GoalReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val goalTitle = inputData.getString("GOAL_TITLE") ?: return Result.failure()
        NotificationHelper.showNotification(
            applicationContext,
            "Hedef Hatırlatıcı",
            "🎯 Hedef zamanı: $goalTitle"
        )
        return Result.success()
    }
}
