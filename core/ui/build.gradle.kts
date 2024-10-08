plugins {
    alias(libs.plugins.tweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.ui"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.designSystem)
    api(projects.core.common)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.lottie.animation)
    implementation(libs.card.view)
    implementation(libs.material)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}