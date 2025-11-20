plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.aemarkov.lab9"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aemarkov.lab9"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // URL сервера из переменной окружения или gradle.properties, по умолчанию для эмулятора
        val apiUrl = project.findProperty("API_BASE_URL") as String?
            ?: System.getenv("API_BASE_URL")
            ?: "http://10.0.2.2:8080/api"
        
        buildConfigField("String", "API_BASE_URL", "\"$apiUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // Retrofit для работы с API
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    
    // Coroutines для асинхронности
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // RecyclerView
    implementation(libs.androidx.recyclerview)
    
    // WorkManager для уведомлений
    implementation(libs.androidx.work.runtime.ktx)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}