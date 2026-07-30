plugins {
    id("com.android.library")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        all {
            consumerProguardFiles("proguard-rules.pro")
        }
    }
}

kotlin {
    jvmToolchain(21)
}
