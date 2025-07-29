// app/src/main/java/com/goaltrackr/util/Logger.kt
package com.goaltrackr.util

import android.util.Log

// Uygulama genelinde loglama için basit bir yardımcı sınıf.
// Debug modunda logları gösterir, Release modunda gizler.
object Logger {
    private const val TAG = "GoalTrackrApp" // Varsayılan log etiketi

    fun log(tag: String = TAG, message: String) {
        // BuildConfig.DEBUG, uygulamanın debug modunda olup olmadığını gösterir.
        // Gerçek projede BuildConfig.DEBUG kontrolü eklenmelidir.
        // Şimdilik her zaman loglayalım veya manuel olarak kontrol edelim.
        Log.d(tag, message)
    }

    fun error(tag: String = TAG, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }
}
