// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    // Available on the classpath so :app can apply it conditionally once
    // google-services.json is added (see app/build.gradle.kts).
    alias(libs.plugins.google.services) apply false
}

