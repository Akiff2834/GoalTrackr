package com.goaltrackr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.goaltrackr.util.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Hedef Hatırlatması"
        val dueDate = intent.getStringExtra("dueDate")
        val description = intent.getStringExtra("description")

        val message = when {
            !description.isNullOrBlank() -> description
            !dueDate.isNullOrBlank() -> "Hedefin bitiş tarihi yaklaşıyor: $dueDate"
            else -> "Bir hedefin süresi dolmak üzere!"
        }

        // Bildirimi göster
        NotificationHelper.showNotification(context, title, message)
    }
}
