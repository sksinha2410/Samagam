plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ingray.samagam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ingray.samagam"
        minSdk = 24
        targetSdk = 34
        versionCode = 13
        versionName = "2.3"

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

    buildFeatures {
        compose = true  // Enable Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"  // Adjust based on latest compatible version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.google.firebase:firebase-messaging:24.0.3")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")

    // Shimmer effect library (for Compose and Views)
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.7.4")  // Core Compose UI
    implementation("androidx.compose.material:material:1.7.4")  // Material Design for Compose
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.4")  // Tooling support
    implementation("androidx.activity:activity-compose:1.9.3")  // Activity support for Compose

    // Jetpack Compose tooling (Optional for debugging)
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.4")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Additional libraries
    implementation("com.github.kittinunf.fuel:fuel:3.0.0-alpha1")
    implementation("net.orandja.shadowlayout:shadowlayout:1.0.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
