@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.jfrog.org/libs-snapshot")
        maven("https://www.jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://www.jitpack.io")
        maven("https://oss.jfrog.org/libs-snapshot")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "TWeather"
include(":app")

//Feature module
include(":feature:home")
include(":feature:weather-details")
include(":feature:city")
include(":feature:city-search")
include(":feature:daily-forecast")
include(":feature:daily-forecast-details")

//Core module
include(":core:cache")
include(":core:common")
include(":core:data")
include(":core:design-system")
include(":core:di")
include(":core:domain")
include(":core:model:api-response")
include(":core:model:entity")
include(":core:shared-pref")
include(":core:ui")