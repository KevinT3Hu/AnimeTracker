import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "me.kht.animetracker"
    compileSdk = 33

    defaultConfig {
        applicationId = "me.kht.animetracker"
        minSdk = 28
        targetSdk = 33
        versionCode = 20
        versionName = "v1.5.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss'Z'")
        val time = LocalDateTime.now(ZoneId.of("Etc/GMT+8")).format(dateTimeFormatter)
        setProperty("archivesBaseName", "$applicationId-$versionName-$time")
        buildConfigField("String", "BUILD_TIME", "\"$time\"")
        buildConfigField("String", "PROJECT_URL", "\"https://github.com/KevinT3Hu/AnimeTracker\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            versionNameSuffix = "-r"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    val firebaseBOM = platform("com.google.firebase:firebase-bom:32.0.0")
    implementation(firebaseBOM)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    val roomVersion = "2.5.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    val composeBOM = platform("androidx.compose:compose-bom:2023.05.01")
    implementation(composeBOM)
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    androidTestImplementation(composeBOM)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation(composeBOM)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("androidx.core:core-ktx:1.11.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.navigation:navigation-compose:2.7.0-alpha01")
    implementation("com.google.accompanist:accompanist-flowlayout:0.31.1-alpha")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-RC")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil:2.3.0")
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}