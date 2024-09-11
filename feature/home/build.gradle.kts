plugins {
    alias(libs.plugins.tweather.android.feature)
}

android {
    namespace = "com.tasnimulhasan.home"
}

dependencies {
    implementation(libs.shimmer)
    implementation(libs.androidx.palette)
    implementation(libs.glide)
    implementation(libs.lottie.animation)
    implementation(libs.bundles.play.services.maps)
}