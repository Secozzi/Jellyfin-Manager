import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqldelight)
}

private val versionStr = "0.1.0"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
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
            implementation(libs.bouncycastle)
            implementation(libs.sqlcipher.android)

            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
            implementation(libs.koin.android.compat)
            implementation(libs.koin.android.startup)
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
            implementation(libs.serialization.json)
            implementation(libs.immutable.collections)

            implementation(libs.navigation.compose)
            implementation(libs.compose.backhandler)
            implementation(libs.compose.materialmotion)
            implementation(libs.bundles.compose.settings)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sshj)
            implementation(libs.compose.ptr)
            implementation(libs.jellyfin.sdk)
            implementation(libs.kmp.uri)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.serialization.xml)

            api(libs.bundles.datastore)
            api(libs.bundles.koin)
        }
        desktopMain.dependencies {
            implementation(libs.sqldelight.desktop)
            implementation(libs.willena.sqlite.jdbc)

            implementation(compose.desktop.currentOs) {
                exclude("org.jetbrains.compose.material")
            }
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xexpect-actual-classes",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
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
        versionName = versionStr
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/**/*.MF"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

sqldelight {
    databases {
        create("JMDatabase") {
            packageName.set("xyz.secozzi.jellyfinmanager.domain.db")
            schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
            verifyMigrations = true
            verifyDefinitions = true
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "xyz.secozzi.jellyfinmanager.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "xyz.secozzi.jellyfinmanager"
            packageVersion = versionStr
        }
    }
}

buildkonfig {
    packageName = "xyz.secozzi.jellyfinmanager"

    defaultConfigs {
        buildConfigField(Type.STRING, "NAME", "Jellyfin Manager", const = true)
        buildConfigField(Type.STRING, "VERSION", versionStr, const = true)
    }
}
