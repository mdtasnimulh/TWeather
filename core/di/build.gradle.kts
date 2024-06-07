plugins {
    alias(libs.plugins.thweather.android.library)
    alias(libs.plugins.thweather.android.hilt)
}

android {
    namespace = "com.tasnimulhasan.di"
}

dependencies {
    api(projects.core.sharedPref)
    implementation(libs.bundles.network.dependencies)
    implementation(libs.bundles.play.services.maps)
    implementation(libs.timber)
}