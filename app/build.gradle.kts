import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.pixelpioneer.moneymaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pixelpioneer.moneymaster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OCR_SPACE_API_KEY",
            "\"${project.properties["OCR_SPACE_API_KEY"]}\""
        )

        buildConfigField(
            "String",
            "COINCAP_API_KEY",
            "\"${project.properties["COINCAP_API_KEY"]}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += "**/libimage_processing_util_jni.so"
            pickFirsts += "**/libc++_shared.so"
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }
}

dependencies {
    // Basis-Abhängigkeiten
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation für Compose
    implementation(libs.androidx.navigation.compose)

    // Room für Datenbank
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.ads.mobile.sdk)
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.ui.text.google.fonts)
    ksp(libs.androidx.room.compiler)

    // ViewModel und LiveData
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coroutines für async/await
    implementation(libs.kotlinx.coroutines.play.services)

    // Coil für Bild-Handling
    implementation(libs.coil.compose)

    // Material Icons Extended (für mehr Icons)
    implementation(libs.androidx.material.icons.extended)

    // Für Datums- und Zeitformatierung
    implementation(libs.kotlinx.datetime)

    // Retrofit für Netzwerkaufrufe
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.mpandroidchart)

    // ML Kit Text Recognition
    implementation(libs.play.services.mlkit.text.recognition)

    // ML Kit Text Recognition
    implementation(libs.text.recognition)

    // Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Permissions
    implementation(libs.androidx.activity.compose)

    // RecyclerView hinzufügen
    implementation(libs.androidx.recyclerview)

    // HTTP Client für Multipart Uploads
    implementation(libs.okhttp)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.config)
    implementation(libs.firebase.appdistribution)

    // exifinterface für EXIF-Daten
    implementation(libs.androidx.exifinterface)


    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}