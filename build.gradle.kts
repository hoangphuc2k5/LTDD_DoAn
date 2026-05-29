// Top-level build file — Tegram (MVVM + Clean Architecture)
// Declares all plugins used by subprojects without applying them here.
plugins {
    // Android
    alias(libs.plugins.android.application) apply false

    // Kotlin
    // kotlin-android is applied transitively by kotlin-compose
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false

    // Hilt — Dependency Injection (Dagger)
    alias(libs.plugins.hilt.android.plugin) apply false

    // Google Services — Firebase
    alias(libs.plugins.google.services) apply false
}