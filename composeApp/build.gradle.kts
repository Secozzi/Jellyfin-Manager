import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.material3.android)
            implementation(libs.androidx.material3)

            implementation(libs.koin.android)
            implementation(libs.koin.android.compat)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.immutable.collections)

            implementation(libs.bundles.ktor)
            // implementation(libs.ktor.client.okhttp)
            implementation(libs.bundles.compose.settings)

            api(libs.bundles.datastore)
            api(libs.bundles.koin)
            implementation(libs.bundles.voyager)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material")
            }
            implementation(compose.preview)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.compose.window)
        }
    }
}

android {
    namespace = "xyz.secozzi.jellyfinmanager"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "xyz.secozzi.jellyfinmanager"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "xyz.secozzi.jellyfinmanager.MainKt"

        nativeDistributions {
            modules("java.compiler", "java.instrument" , "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "xyz.secozzi.jellyfinmanager"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules.pro")
        }
    }
}
