plugins {
    id("randomchampion.jvm.library")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.annotation)

    kapt(libs.hilt.compiler)
}
