import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(isdlibs.plugins.kotlinMultiplatform)
  alias(isdlibs.plugins.composeMultiplatform)
  alias(isdlibs.plugins.compose.compiler)
  alias(isdlibs.plugins.androidApplication)
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

  jvm()

  js {
    browser()
    binaries.executable()
  }

  @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
      }
    }

    binaries.executable()
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
      implementation(project(":datetime-wheel-picker"))

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
      implementation(isdlibs.androidx.compose.activity)
    }

    jvmMain.dependencies {
//      implementation(compose.desktop.common)
      implementation(compose.desktop.currentOs)
    }

    jsMain.dependencies {
      implementation(compose.html.core)
    }

    wasmJsMain.dependencies {
    }

    iosMain.dependencies {
    }

  }
}

android {
  namespace = "com.intsoftdev.datetimewheelpicker"
  compileSdk = 34

  defaultConfig {
    minSdk = 24
    targetSdk = 34

    applicationId = "com.intsoftdev.datetimewheelpicker.androidApp"
    versionCode = 1
    versionName = "1.0.0"
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

compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.intsoftdev.datetimewheelpicker.desktopApp"
      packageVersion = "1.0.0"
    }
  }
}
