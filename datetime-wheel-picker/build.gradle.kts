import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(isdlibs.plugins.maven.publish)
    alias(isdlibs.plugins.kotlinMultiplatform)
    alias(isdlibs.plugins.composeMultiplatform)
    alias(isdlibs.plugins.compose.compiler)
    alias(isdlibs.plugins.androidLibrary)
}

group = "com.intsoftdev"
version = "1.0.2"

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = group.toString(),
        artifactId = "datetime-wheel-picker",
        version = version.toString()
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("DateTime Wheel Picker")
        description.set("Wheel Date & Time Picker in Compose Multiplatform")
        url.set("https://github.com/azaka01/compose-datetime-wheel-picker")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("azaka01")
                name.set("A Zaka")
                email.set("az@intsoftdev.com")
            }
        }
        scm {
            url.set("https://github.com/azaka01/compose-datetime-wheel-picker.git")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // Enable GPG signing for all publications
    signAllPublications()
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.intsoftdev.datetimewheelpicker"
    compileSdk = isdlibs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = isdlibs.versions.minSdk.get().toInt()
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        resources.srcDirs("src/commonMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        @Suppress("OPT_IN_USAGE")
        unitTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(isdlibs.kotlinx.dateTime)
            implementation(isdlibs.napier.logger)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
        }

        iosMain.dependencies {
        }
    }
}