plugins {
    alias(libs.plugins.tweather.android.library)
    alias(libs.plugins.tweather.android.hilt)
}

android {
    namespace = "com.tasnimulhasan.domain"
}

dependencies {
    implementation(libs.bundles.androidx.core.dependencies)
    implementation(libs.kotlin.coroutines)
    api(projects.core.model.entity)
    implementation(libs.bundles.room.dependencies)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}