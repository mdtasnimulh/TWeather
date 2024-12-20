import com.tasnimulhasan.tweather.AppConfig

plugins {
    alias(libs.plugins.tweather.android.application)
    alias(libs.plugins.tweather.android.hilt)
    alias(libs.plugins.tweather.android.navigation)
}

android {
    namespace = AppConfig.APPLICATION_ID
    compileSdk = AppConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = AppConfig.APPLICATION_ID
        minSdk = AppConfig.MIN_SDK_VERSION
        targetSdk = AppConfig.TARGET_SDK_VERSION
        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("app-credential/tweather.jks")
            storePassword = "tweather#107"
            keyAlias = "tweather"
            keyPassword = "tweather#107"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        create("qa") {
            initWith(getByName("release"))
            matchingFallbacks.add("release")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    with(projects) {
        implementation(feature.home)
        implementation(feature.weatherDetails)
        implementation(feature.dailyWeatherDetails)
        implementation(feature.city)
        implementation(feature.citySearch)
        implementation(feature.dailyForecast)
        implementation(feature.dailyForecastDetails)
    }

    with(projects.core){
        implementation(di)
        implementation(common)
        implementation(designSystem)
        implementation(ui)
        implementation(data)
        implementation(domain)
        implementation(cache)
        implementation(sharedPref)
        implementation(model.entity)
        implementation(model.apiResponse)
    }

    implementation(libs.bundles.androidx.core.dependencies)
    implementation(libs.bundles.androidx.lifecycle.dependencies)
    implementation(libs.bundles.rxjava3.dependencies)
    implementation(libs.bundles.androidx.navigation.dependencies)

    implementation(libs.timber)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.work)
    implementation(libs.hilt.ext.work)

    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
    kspTest(libs.hilt.compiler)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.runner)
}