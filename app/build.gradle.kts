import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize") // For passing data between destinations if needed
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
val secretsPropertiesFile = rootProject.file("secrets.properties")
val secretsProperties = Properties()
secretsProperties.load(FileInputStream(secretsPropertiesFile))
android {

    namespace = "com.aki.flashnews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aki.flashnews"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildFeatures.buildConfig= true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NEWS_API_KEY", secretsProperties.getProperty("NEWS_API_KEY"))
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.room.runtime) // Or latest
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx) // Kotlin Extensions and Coroutines support

    implementation(libs.hilt.android) // Or latest
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // Hilt ViewModel integration with Compose Navigation

    implementation(libs.moshi.kotlin) // Or latest
    ksp(libs.moshi.kotlin.codegen)

    // Retrofit & Networking
    implementation(libs.retrofit) // Or latest
    implementation(libs.converter.moshi) // Moshi converter
    // implementation("com.squareup.retrofit2:converter-gson:2.9.0") // OR Gson converter
    implementation(libs.okhttp) // Or latest
    implementation(libs.logging.interceptor) // Optional: For logging network requests

    implementation(libs.coil.compose) // Image loading

}