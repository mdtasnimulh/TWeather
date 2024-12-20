plugins {
    alias(libs.plugins.tweather.android.library)
    alias(libs.plugins.tweather.android.hilt)
}

android {
    namespace = "com.tasnimulhasan.data"
}

dependencies {
    implementation(libs.bundles.network.dependencies)
    implementation(libs.bundles.rxjava3.dependencies)
    implementation(libs.kotlin.coroutines)
    implementation(libs.dateced)

    implementation(projects.core.domain)
    implementation(projects.core.di)
    implementation(projects.core.cache)
    implementation(projects.core.common)
    implementation(projects.core.model.entity)
    implementation(projects.core.model.apiResponse)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}