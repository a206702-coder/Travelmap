plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

android {
    namespace = "com.example.travelmap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.travelmap"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // 原有基础Compose依赖
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ========== Lab4 新增必备依赖 ==========
    // 1. Compose 页面导航 (Update to 2.8.0+ for type-safe navigation)
    implementation("androidx.navigation:navigation-compose:2.8.7")
    // 2. ViewModel Compose 生命周期数据管理
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // 3. Icons 完整图标库
    implementation(libs.androidx.compose.material.icons.extended)
    // 4. Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}
