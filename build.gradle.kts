import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.versions)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return !stableKeyword && !regex.matches(version)
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    checkForGradleUpdate = true
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
