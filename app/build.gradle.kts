plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {

    packagingOptions {
        resources {
            // Specify what to do with the META-INF/DEPENDENCIES file
            pickFirst("META-INF/DEPENDENCIES")
        }
    }

    namespace = "com.example.moble_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moble_project"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("org.chromium.net:cronet-embedded:113.5672.61")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.biometric:biometric:1.1.0")

    // Retrofit 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")

// Scalars 변환기 라이브러리
    implementation ("com.squareup.retrofit2:converter-scalars:2.6.4")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    implementation ("org.apache.httpcomponents:httpmime:4.5.13")

    implementation ("com.amazonaws:aws-android-sdk-core:2.73.0")
    implementation ("com.amazonaws:aws-android-sdk-s3:2.73.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
//    카메라
    implementation ("androidx.camera:camera-core:1.2.1")
    implementation ("androidx.camera:camera-camera2:1.2.1")
    implementation ("androidx.camera:camera-lifecycle:1.2.1")
    implementation ("androidx.camera:camera-view:1.2.1")
    implementation ("androidx.lifecycle:lifecycle-process:2.4.0") // 또는 최신 버전

    //firebasemlkit라이브러리추가한
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-ml-vision:24.0.3")
    implementation ("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.google.firebase:firebase-ml-vision-face-model:20.0.1")


}