import com.android.build.gradle.LibraryExtension
import com.tasnimulhasan.thweather.AppConfig
import com.tasnimulhasan.thweather.configureKotlinAndroid
import com.tasnimulhasan.thweather.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("thweather.android.lint")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    testInstrumentationRunner = AppConfig.testInstrumentationRunner
                    consumerProguardFile("consumer-rules.pro")
                }
            }

            dependencies {
                add("testImplementation", libs.findLibrary("test.junit").get())
                add("androidTestImplementation", libs.findLibrary("test.extjunit").get())
                add( "androidTestImplementation",libs.findLibrary("test.espresso").get())
            }
        }
    }
}