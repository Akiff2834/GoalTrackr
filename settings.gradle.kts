// settings.gradle.kts
// Bu dosya, projenin genel ayarlarını ve modüllerini tanımlar.

pluginManagement {
    repositories {
        // Gradle eklentilerini bulmak için kullanılan depolar
        gradlePluginPortal() // Gradle Plugin Portal
        google {
            // Sadece Android, Google ve AndroidX gruplarını dahil et
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral() // Maven Central deposu
    }
    plugins {
        // Uygulama genelinde kullanılacak eklentilerin ID'leri ve versiyonları
        // Bu eklentiler, app/build.gradle.kts dosyasında 'id("...")' olarak referans alınır.
        id("com.android.application") version "8.9.1"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
        id("com.google.gms.google-services") version "4.4.2" // Firebase için gerekli
        id("com.google.firebase.crashlytics") version "2.9.9" // Firebase Crashlytics için gerekli
    }
}

dependencyResolutionManagement {
    // Proje depolarının nasıl çözümleneceğini belirler
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Bağımlılıkları bulmak için kullanılan depolar
        google() // Google'ın Maven deposu
        mavenCentral() // Maven Central deposu
    }

    // Version Catalogs bloğu kaldırılmıştır.
    // Bağımlılıklar doğrudan app/build.gradle.kts içinde tanımlanacaktır.
}

// Projenin kök adı
rootProject.name = "GoalTrackr"
// Dahil edilecek modüller
include(":app")
