import java.util.Properties

plugins {
    id("randomchampion.android.application")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.spiderbiggen.randomchampionselector"

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystorePropertiesFile.inputStream().use(keystoreProperties::load)

                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            } else {
                val path = System.getenv("SIGNING_STORE_PATH")
                if (!path.isNullOrEmpty()) {
                    storeFile = file(path)
                    storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                    keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                    keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
                }
            }
        }
    }

    defaultConfig {
        applicationId = "com.spiderbiggen.randomchampionselector"
        versionCode = 102000
        versionName = "1.2.0"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
}

kapt {
    javacOptions {
        // https://github.com/google/dagger/issues/970
        option("-Adagger.validateTransitiveComponentDependencies=DISABLED")
    }
}
