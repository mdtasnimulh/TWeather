plugins {
    alias(libs.plugins.thweather.android.feature)
}

android {
    namespace = "com.tasnimulhasan.home"
}

dependencies {
    implementation(libs.shimmer)
}