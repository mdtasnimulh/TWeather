plugins {
    alias(libs.plugins.thweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.designsystem"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.bundles.androidx.core.dependencies)
    implementation(libs.bundles.android.responsive.size.dependencies)
}