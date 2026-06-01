// ---- Plugins ----
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // kotlin-android is already applied by kotlin-compose — do NOT add both
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)                         // KSP — annotation processing (Hilt, Room, Moshi)
    alias(libs.plugins.hilt.android.plugin)        // Hilt DI
}

// ---- Android Configuration ----
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}
val apiBaseUrl = localProperties.getProperty("apiBaseUrl") ?: "http://10.0.2.2:3001/"

android {
    namespace = "com.example.tegram"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tegram"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "api_base_url", apiBaseUrl)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        resValues = true
    }

}

// ---- Dependencies ----
dependencies {

    // ── Compose BOM (manages all Compose library versions) ──
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ── AndroidX Core ──
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ── Hilt — Dependency Injection (Dagger) ──
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // ── Room — Local SQLite Database ──
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)                  // Coroutines extensions
    ksp(libs.room.compiler)

    // ── Retrofit2 + OkHttp3 — Networking ──
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)  // JSON ↔ Kotlin via Moshi
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // ── Moshi — JSON Parser ──
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)              // Kotlin-aware adapters
    ksp(libs.moshi.kotlin.codegen)                 // Code-gen for @JsonClass

    // ── Firebase (versions managed by BOM) ──
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)         // Email + Google Sign-In
    implementation(libs.firebase.storage.ktx)      // Profile image upload
    implementation(libs.firebase.messaging.ktx)    // FCM push notifications

    // ── Kotlin Coroutines ──
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ── Lifecycle / ViewModel ──
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ── Jetpack Navigation Component (Compose) ──
    implementation(libs.androidx.navigation.compose)

    // ── WorkManager ──
    implementation(libs.androidx.work.runtime.ktx)

    // ── DataStore Preferences ──
    implementation(libs.androidx.datastore.preferences)

    // ── Coil — Image Loading ──
    implementation(libs.coil.compose)

    // ── Google Play Services — Auth (Google Sign-In) ──
    implementation(libs.play.services.auth)

    // ── Testing ──
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

// Apply Google Services only when google-services.json is present.
// This keeps local builds working before Firebase is configured.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
} else {
    logger.warn("google-services.json not found in app/. Firebase resources will not be generated for this build.")
}
