import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(isdlibs.plugins.kotlinMultiplatform)
    alias(isdlibs.plugins.composeMultiplatform)
    alias(isdlibs.plugins.compose.compiler)
    alias(isdlibs.plugins.androidApplication)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.intsoftdev.datetimewheelpicker"
    compileSdk = isdlibs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.intsoftdev.datetimewheelpicker.androidApp"
        minSdk = isdlibs.versions.minSdk.get().toInt()
        targetSdk = isdlibs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
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
            implementation(project(":datetime-wheel-picker"))

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(isdlibs.kotlinx.dateTime)
            implementation(isdlibs.napier.logger)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(isdlibs.androidx.compose.activity)
        }

        iosMain.dependencies {
        }
    }
}

dependencies {
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.7.6")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.6")
}