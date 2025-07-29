// app/src/main/java/com/goaltrackr/GoalTrackrApp.kt
package com.goaltrackr

import android.app.Application
import com.goaltrackr.util.Logger

// Uygulamanın ana Application sınıfı.
// Uygulama başlatıldığında ilk çalışan yerdir.
// Firebase, Room veritabanı ve Retrofit gibi global bileşenlerin başlatılması için kullanılır.
class GoalTrackrApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.log("GoalTrackrApp", "Uygulama başlatılıyor...")

        // Firebase Crashlytics ve Analytics otomatik olarak başlatılır
        // build.gradle.kts'e eklenen eklentiler sayesinde.
        // Eğer özel bir başlatma gerekiyorsa buraya eklenebilir.

        // Room veritabanı veya Retrofit istemcisi gibi bağımlılıkların
        // Dependency Injection (örneğin Hilt) ile sağlanması önerilir.
        // Şimdilik basitçe burada loglama yapıyoruz.
    }
}
