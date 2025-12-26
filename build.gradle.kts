import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.android.application)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

//    js {
//        browser()
//        binaries.executable()
//    } //TODO: handle charts

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val desktopMain by creating {
            kotlin.srcDir("src/desktopMain/kotlin")
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.ktor.client.cio)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
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
            implementation(libs.material3.adaptive)
        }

        desktopMain.dependsOn(commonMain.get())
        androidMain.get().dependsOn(commonMain.get())
        jvmMain.get().dependsOn(desktopMain)
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }
        webMain.get().dependsOn(desktopMain)
        webMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        wasmJsMain.get().dependsOn(webMain.get())
    }
}

android {
    namespace = "org.nara.mia_native"
    compileSdk = 36


    defaultConfig {
        applicationId = "org.nara.mia_native"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.nara.mia_native"
            packageVersion = "1.0.0"
        }
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "webMain",
        directoryProvider = provider { layout.projectDirectory.dir("src/webMain/composeResources") }
    )
}