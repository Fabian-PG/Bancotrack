plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bancotrack"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.bancotrack"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ➡️ NECESARIO PARA USAR RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ➡️ NECESARIO PARA USAR CardView (para el diseño de las tarjetas)
    implementation("androidx.cardview:cardview:1.0.0")
}