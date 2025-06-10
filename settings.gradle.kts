rootProject.name = "datetime-wheel-picker"
include(":sample:composeApp")
include(":datetime-wheel-picker")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            content {
                includeGroupAndSubgroups("com.intsoftdev")
            }
        }
    }
    versionCatalogs {
        create("isdlibs") {
            from("com.intsoftdev:isddependencies:1.0.0-ALPHA-18-SNAPSHOT")
        }
    }
}
