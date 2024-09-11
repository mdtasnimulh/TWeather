plugins {
    alias(libs.plugins.thweather.android.library)
}

android {
    namespace = "com.tasnimulhasan.common"
}

dependencies {
    implementation(projects.core.sharedPref)
    implementation(projects.core.designSystem)
    implementation(projects.core.model.entity)

    implementation(libs.timber)
    implementation(libs.bundles.androidx.core.dependencies)
    implementation(libs.bundles.androidx.lifecycle.dependencies)
    implementation(libs.bundles.androidx.navigation.dependencies)
    implementation(libs.bundles.rxjava3.dependencies)
    implementation(libs.bundles.play.services.maps)
    implementation(libs.picasso)
    implementation(libs.dateced)
    implementation(libs.date.picker)
    implementation(libs.glide)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.core.splashscreen)

    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.extjunit)
    androidTestImplementation(libs.test.espresso)
}