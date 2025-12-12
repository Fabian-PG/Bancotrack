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
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //libreria de google
    implementation("com.google.android.gms:play-services-location:21.3.0")
    //LIBRERIA DE OSMDROID
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
// o la versión que estés usando
// **IMPORTANTE: Añadir la librería de contribuciones para RoadManager**
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.18")
// (Opcional, si usas Mapsforge)
    implementation("org.osmdroid:osmdroid-wms:6.1.18")
// Dependencia para hacer peticiones HTTP (Volley)
    implementation("com.android.volley:volley:1.2.1")


}