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

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}