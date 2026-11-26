plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.reverender.r95"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.reverender.r95"
        minSdk = 21
        targetSdk = 35
        // every CI build gets a higher number, so Android accepts it as an update
        val run = (System.getenv("GITHUB_RUN_NUMBER") ?: "0").toIntOrNull() ?: 0
        versionCode = 100 + run
        versionName = "0.2.$run-native"
    }

    // A fixed release key (from the R95_* secrets in CI). Without it the build falls back to the
    // throw-away debug key, which changes per CI machine and makes Play Protect wary.
    val ks = System.getenv("R95_KEYSTORE")?.let { file(it) }?.takeIf { it.exists() }
    signingConfigs {
        if (ks != null) create("release") {
            storeFile = ks
            storePassword = System.getenv("R95_STORE_PASS")
            keyAlias = System.getenv("R95_KEY_ALIAS")
            keyPassword = System.getenv("R95_KEY_PASS")
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Debug-signed so "Build > Generate APKs" gives an installable release APK straight away.
            // Swap for your own keystore before uploading to the Play Store.
            signingConfig = if (ks != null) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // vhs: the VHS/VCR launcher. Its own app id (com.reverender.r95.vhs), so it installs next to REVERENDER_95.
    // The desktop, wallpapers, games, widgets etc. that share the source are left out by R8: nothing reaches them.
    flavorDimensions += "dist"
    productFlavors {
        create("vhs") {
            dimension = "dist"; applicationIdSuffix = ".vhs"; versionNameSuffix = "-vhs"
            buildConfigField("boolean", "FULL", "false"); buildConfigField("boolean", "VHS_ONLY", "true")
        }
    }
    lint {
        // Media3's @UnstableApi opt-ins are handled with @OptIn; don't let lint block release builds.
        disable += "UnsafeOptInUsageError"
        abortOnError = false
        textReport = true
        error += "NewApi"
    }
}

// VHS-only APK: drop the native engine and font it never loads (Revy's JS engine, the emoji font)
androidComponents {
    onVariants(selector().withFlavor("dist" to "vhs")) { v ->
        v.packaging.jniLibs.excludes.addAll(listOf("**/libquickjs.so")) // (the VHS GAME DECK uses libretrodroid)
        v.androidResources.ignoreAssetsPatterns.addAll(listOf("NotoColorEmojiCompat.ttf", "<dir>revy"))
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.core:core-ktx:1.15.0")
    // Bundled emoji font so the Win95 icons (emoji) look the same on Android 5.0 as on Android 15.
    implementation("androidx.emoji2:emoji2:1.5.0")
    implementation("androidx.emoji2:emoji2-bundled:1.5.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    val media3 = "1.5.1"
    implementation("androidx.media3:media3-exoplayer:$media3")
    implementation("androidx.media3:media3-session:$media3")
    implementation("androidx.media3:media3-common:$media3")
    implementation("androidx.media3:media3-ui:$media3")
    implementation("androidx.media3:media3-cast:$media3")
    implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
    implementation("androidx.mediarouter:mediarouter:1.7.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // Revy: runs the site's own built-in brain JavaScript (no WebView)
    implementation("app.cash.quickjs:quickjs-android:0.9.2")

    // RETRO_EMU_95: native libretro frontend (same cores as the website's EmulatorJS). GPL-3.0.
    implementation("com.github.Swordfish90:LibretroDroid:0.14.0")
    // VIDEO_STREAM_95: YouTube stream addresses for the native player (the site uses YouTube's embed). GPL-3.0.
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.27.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.1.5")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
}
