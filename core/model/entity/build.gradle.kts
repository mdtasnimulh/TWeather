plugins {
    alias(libs.plugins.tweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.entity"
}

dependencies {
    implementation(libs.room.common)
    implementation(libs.gson)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}