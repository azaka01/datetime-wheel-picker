import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(isdlibs.plugins.kotlinMultiplatform)
    alias(isdlibs.plugins.composeMultiplatform)
    alias(isdlibs.plugins.compose.compiler)
    alias(isdlibs.plugins.androidLibrary)
    alias(isdlibs.plugins.maven.publish)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
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
        all {
            languageSettings {
//        optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
//      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
//      implementation(compose.components.resources)
            implementation(isdlibs.kotlinx.dateTime)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
        }

        jvmMain.dependencies {
        }

        jsMain.dependencies {
        }

        wasmJsMain.dependencies {
        }

        iosMain.dependencies {
        }

    }
}

android {
    namespace = "dev.azaka01.datetimewheelpicker"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
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

// TODO set up publishing
// https://vanniktech.github.io/gradle-maven-publish-plugin/central/
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}