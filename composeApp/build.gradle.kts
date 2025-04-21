import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // Configure desktop target for macOS
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.moko.mvvm.core)
            implementation(libs.moko.mvvm.compose)
            implementation(libs.compose.material3)
        }
        
        val desktopTest by getting
    }
}

android {
    namespace = "com.tatav.pomodoro"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    defaultConfig {
        applicationId = "com.tatav.pomodoro"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Desktop application configuration
compose.desktop {
    application {
        // Main class that will be executed when running the application
        mainClass = "com.tatav.pomodoro.MainKt"
        
        nativeDistributions {
            // Available distribution formats
            targetFormats(
                TargetFormat.Dmg,  // macOS installer
                TargetFormat.Msi   // Windows installer
            )
            
            // Application metadata
            packageName = "Pomodoro"
            packageVersion = "1.0.0"
            
            // Windows specific configuration
            windows {
                shortcut = true
                menuGroup = "Pomodoro Timer"
                upgradeUuid = "9FC1E89C-5EF9-4CD3-8B9C-83011BA4316A"
            }
            
            // macOS specific configuration
            macOS {
                bundleID = "com.tatav.pomodoro"
            }
        }
    }
}

/*
 * ====================================================
 * USEFUL GRADLE COMMANDS
 * ====================================================
 * 
 * Development Commands:
 * --------------------
 * ./gradlew :composeApp:run           - Run the app directly for development
 * ./gradlew :composeApp:clean         - Clean the build
 * ./gradlew :composeApp:build         - Build the project
 * ./gradlew :composeApp:test          - Run tests
 * 
 * Android Commands:
 * ----------------
 * ./gradlew :composeApp:installDebug  - Install debug version on connected Android device
 * ./gradlew :composeApp:installRelease - Install release version on connected Android device
 * 
 * Desktop Distribution Commands:
 * ----------------------------
 * ./gradlew :composeApp:packageDmg    - Create macOS installer (.dmg)
 * ./gradlew :composeApp:packageMsi    - Create Windows installer (.msi)
 * ./gradlew :composeApp:packageUberJarForCurrentOS - Create a runnable JAR for current OS
 * 
 * Combined Commands:
 * ----------------
 * ./gradlew clean :composeApp:packageDmg - Clean and create macOS installer
 * ./gradlew clean :composeApp:packageMsi - Clean and create Windows installer
 * 
 * Helpful Commands:
 * ----------------
 * ./gradlew tasks                     - List all available tasks
 * ./gradlew :composeApp:tasks         - List tasks for composeApp module
 * ./gradlew --scan                    - Generate a build scan
 * 
 * Note: Replace 'composeApp' with your module name if different
 */
