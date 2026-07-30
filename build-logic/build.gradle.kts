plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)

    // Applied by the convention plugins. Owning them here keeps every plugin that
    // pulls in the Kotlin Gradle plugin on this one classloader; applying them from
    // each module's plugins block loads it once per module instead.
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.navigation.safeargs.gradlePlugin)
}
