plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.vz.skne"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vz.skne"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"${System.getenv("SPOTIFY_CLIENT_ID") ?: "your_client_id_here"}\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"spotify-sdk://auth\"")
        buildConfigField("String", "SPOTIFY_CLIENT_SECRET", "\"${System.getenv("SPOTIFY_CLIENT_SECRET") ?: "your_client_secret_here"}\"")
        manifestPlaceholders["redirectSchemeName"] = "spotify-sdk"
        manifestPlaceholders["redirectHostName"] = "auth"
        manifestPlaceholders["redirectPathPattern"] = ".*"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Retrofit (latest stable version)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Kotlin Coroutines (latest stable version)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Coil for image loading (latest stable version)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Spotify App Remote (.aar - downloaded to libs/)
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))

    // Spotify Auth (latest stable version)
    implementation("com.spotify.android:auth:3.0.0")

    // OkHttp Logging Interceptor for network debugging
    implementation(libs.okhttp.logging)

    // Spotify Web API - Using Retrofit directly instead of wrapper

    // Gson for Spotify SDK serialization (required per tutorial)
    implementation("com.google.code.gson:gson:2.11.0")

    // Navigation Compose (latest stable version)
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Google Fonts for Delius font (latest stable version)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.9.2")
}
