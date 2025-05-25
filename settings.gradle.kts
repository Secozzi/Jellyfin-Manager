rootProject.name = "jellyfin-manager"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            content {
                // Only allow SDK snapshots
                includeVersionByRegex("org\\.jellyfin\\.sdk", ".*", "master-SNAPSHOT")
            }
        }
    }
}

include(":composeApp")
