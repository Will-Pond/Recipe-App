buildscript {
    dependencies {
        classpath(libs.hilt.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.google.ksp) apply false
}
