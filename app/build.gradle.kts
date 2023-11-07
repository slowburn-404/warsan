plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.warsan"
    compileSdk = 34
    packaging {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("mozilla/public-suffix-list.txt")
        resources.excludes.add("kotlin/internal/internal.kotlin_builtins")
        resources.excludes.add("META-INF/*.kotlin_module")
        resources.excludes.add("meta-inf/kotlin-stdlib-common.kotlin_module")
        resources.excludes.add("meta-inf/kotlin-stdlib-jdk7.kotlin_module")
        resources.excludes.add("meta-inf/kotlin-stdlib-jdk8.kotlin_module")
        resources.excludes.add("meta-inf/kotlin-stdlib.kotlin_module")
        resources.excludes.add("kotlin/annotation/annotation.kotlin_builtins")
        resources.excludes.add("kotlin/collections/collections.kotlin_builtins")
        resources.excludes.add("kotlin/coroutines/coroutines.kotlin_builtins")
        resources.excludes.add("kotlin/internal/internal.kotlin_builtins")
        resources.excludes.add("kotlin/kotlin.kotlin_builtins")
        resources.excludes.add("kotlin/ranges/ranges.kotlin_builtins")
        resources.excludes.add("kotlin/reflect/reflect.kotlin_builtins")
        resources.excludes.add("xsd/catalog.xml")

    }
    defaultConfig {
        applicationId = "com.example.warsan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.material:material:1.10.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

}