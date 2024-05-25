plugins {
    alias(libs.plugins.thweather.android.library)
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
}