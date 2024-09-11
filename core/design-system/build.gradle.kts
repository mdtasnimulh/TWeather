plugins {
    alias(libs.plugins.tweather.android.library)
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
    implementation(libs.androidx.core.splashscreen)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}