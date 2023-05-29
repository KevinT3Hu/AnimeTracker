import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "me.kht.animetracker"
    compileSdk = 33

    defaultConfig {
        applicationId = "me.kht.animetracker"
        minSdk = 28
        targetSdk = 33
        versionCode = 14
        versionName = "v1.4.2"

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

    implementation("androidx.navigation:navigation-compose:2.6.0-rc01")
    implementation("com.google.accompanist:accompanist-flowlayout:0.31.1-alpha")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-RC")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui-graphics")
    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))

    val room_version = "2.5.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")


    val compose_version = "1.4.2"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("androidx.core:core-ktx:1.11.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.compose.material3:material3:1.1.0-rc01")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil:2.3.0")
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
}