plugins {
    alias(libs.plugins.thweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.entity"
}

dependencies {
    implementation(libs.room.common)
    implementation(libs.gson)
}