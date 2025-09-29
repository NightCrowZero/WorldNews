plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.worldnews"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.worldnews"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "NEWS_API_KEY", "\"710a9393dd7148ffbc0097ba7a377763\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val roomVersion = "2.6.1"
    val glideVersion = "4.13.0"
    implementation ("com.github.bumptech.glide:glide:$glideVersion")
    kapt ("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")// Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")// Retrofit
    implementation("androidx.room:room-runtime:$roomVersion")// Room
    ksp("androidx.room:room-compiler:$roomVersion") // Room
    implementation("androidx.room:room-ktx:$roomVersion")// Room
    implementation("androidx.navigation:navigation-compose:2.9.5")// Navigation Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")// Lifecycle ViewModel
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}