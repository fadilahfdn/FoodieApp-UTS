plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kelompok.foodieapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.kelompok.foodieapp"
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // untuk membuat request
    implementation("com.google.code.gson:gson:2.10.1") // untuk mengambil data json
//    implementation("com.github.bumptech.glide:glide:4.16.0") // untuk memuat gambar - tidak digunakan lagi, sekarang pakai yang lokal
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") // untuk refresh jika scroll ke atas
    implementation("com.google.android.gms:play-services-location:21.0.1") // untuk permintaan akses lokasi
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}