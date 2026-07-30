plugins {
    id("com.android.application")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}
