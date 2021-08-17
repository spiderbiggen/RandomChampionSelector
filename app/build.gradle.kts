import java.util.Properties
import java.io.FileInputStream
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

// Creates a variable called keystorePropertiesFile, and initializes it to the
// keystore.properties file.
val keystorePropertiesFile = rootProject.file("keystore.properties")
// Initializes a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()
// Loads the keystore.properties file into the keystoreProperties object.
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    compileSdkVersion(30)
    signingConfigs {
        register("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    defaultConfig {
        applicationId = "com.spiderbiggen.randomchampionselector"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 2
        versionName = "1.1"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isDebuggable = false
        }
        create("stage") {
            initWith(getByName("release"))
            applicationIdSuffix = ".staging"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    extra["room_version"] = "2.4.0-alpha04"
    extra["lifecycle_version"] = "2.3.1"
    extra["coroutines_version"] = "1.5.0"
    extra["work_version"] = "2.5.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.activity:activity-ktx:1.3.1")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("com.google.android.material:material:1.4.0")

    implementation("androidx.core:core-ktx:1.6.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${extra["lifecycle_version"]}")

    implementation("com.squareup.okhttp3:logging-interceptor:3.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra["coroutines_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${extra["coroutines_version"]}")

    implementation("androidx.room:room-runtime:${extra["room_version"]}")
    implementation("androidx.room:room-ktx:${extra["room_version"]}")
    kapt("androidx.room:room-compiler:${extra["room_version"]}")
}
