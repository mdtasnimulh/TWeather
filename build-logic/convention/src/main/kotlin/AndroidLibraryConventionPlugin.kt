import com.android.build.gradle.LibraryExtension
import com.tasnimulhasan.tweather.AppConfig
import com.tasnimulhasan.tweather.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("tweather.android.lint")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    testInstrumentationRunner = AppConfig.testInstrumentationRunner
                    consumerProguardFile("consumer-rules.pro")
                }
            }

            dependencies {
                add("testImplementation", kotlin("test"))
            }
        }
    }
}