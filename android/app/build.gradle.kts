plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.grandtour"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grandtour"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-rome"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("GT_KEYSTORE") ?: "release.keystore"
            storeFile = file(keystorePath)
            storePassword = System.getenv("GT_STORE_PWD") ?: ""
            keyAlias = System.getenv("GT_KEY_ALIAS") ?: "grandtour"
            keyPassword = System.getenv("GT_KEY_PWD") ?: ""
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val keystoreFile = file(System.getenv("GT_KEYSTORE") ?: "release.keystore")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/kotlin")
        }
        getByName("test") {
            kotlin.srcDirs("src/test/kotlin")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.nav.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.datastore.preferences)

    implementation(libs.hilt.android)
    implementation(libs.hilt.nav.compose)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
