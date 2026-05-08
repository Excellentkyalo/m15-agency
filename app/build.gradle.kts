plugins {
    id("com.android.application")
    // Removed kotlin.android plugin as it's built-in now
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.google.gms.google.services)
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.example.edusphere"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.edusphere"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ✅ FIXED: Removed the old 'kotlinOptions' block.
    // Modern AGP handles JVM target via compileOptions above.

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ✅ AndroidX Core & Lifecycle
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // ✅ Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ✅ Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ✅ Utilities
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // ✅ Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.11.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ✅ Add these for Cloudinary & JSON
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ✅ Add these for Advanced UX
    implementation("androidx.compose.animation:animation-graphics") // For Lottie-like animations
    implementation("androidx.datastore:datastore-preferences:1.0.0") // For Offline/LocalPrefs
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1") // For Pull-to-Refresh
    implementation("io.coil-kt:coil-compose:2.5.0") // Ensure latest Coil for images

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ✅ Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.3.1")

    implementation("androidx.appcompat:appcompat:1.6.1")


}



