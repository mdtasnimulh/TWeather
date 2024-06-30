plugins {
    alias(libs.plugins.thweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.sharedpref"
}

dependencies {
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}