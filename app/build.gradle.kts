plugins {
    id("com.android.application")
    // AGP 9.x provides built-in Kotlin support for Android modules.
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "org.example.waspsolitaire"
    // Compose 1.12+ requires API 37 for compilation; targetSdk remains 36.
    compileSdk = 37

    defaultConfig {
        // INTERNAL PLACEHOLDER ONLY. Never publish with this applicationId.
        // Verify publisher identity and register the final application ID first.
        applicationId = "org.example.waspsolitaire"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.0-setup"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":game-engine"))

    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
