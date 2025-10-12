import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.compose") version "2.1.21"
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.compose.hot-reload") version "1.0.0-beta05"
    kotlin("plugin.serialization") version "2.1.21"
}

group = "org.nara"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(compose.materialIconsExtended)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.datetime)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.charts)
    implementation(libs.colormath)
    implementation(libs.colormath.ext.jetpack.compose)
}

compose.desktop {
    application {
        mainClass = "MainKt"

//        jvmArgs("-Dawt.toolkit.name=WLToolkit")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "mia_native"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
            isEnabled = false
        }
    }
}
