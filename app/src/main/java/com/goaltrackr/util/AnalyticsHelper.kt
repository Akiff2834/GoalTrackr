package com.goaltrackr.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsHelper {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    fun logGoalCreated(title: String, category: String) {
        val bundle = Bundle().apply {
            putString("goal_title", title)
            putString("goal_category", category)
        }
        analytics.logEvent("goal_created", bundle)
    }

    fun logGoalUpdated(title: String, category: String) {
        val bundle = Bundle().apply {
            putString("goal_title", title)
            putString("goal_category", category)
        }
        analytics.logEvent("goal_updated", bundle)
    }

    fun logGoalDeleted(title: String, category: String) {
        val bundle = Bundle().apply {
            putString("goal_title", title)
            putString("goal_category", category)
        }
        analytics.logEvent("goal_deleted", bundle)
    }
}
