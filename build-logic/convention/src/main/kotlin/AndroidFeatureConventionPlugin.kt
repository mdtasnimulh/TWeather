import com.android.build.gradle.LibraryExtension
import com.tasnimulhasan.thweather.AppConfig
import com.tasnimulhasan.thweather.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("thweather.android.library")
                apply("thweather.android.hilt")
                apply("thweather.android.navigation")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = AppConfig.testInstrumentationRunner
                }
                buildFeatures {
                    viewBinding = true
                }
            }

            dependencies {
                add("implementation", project(":core:di"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:model:entity"))
                add("implementation", project(":core:shared-pref"))

                add("implementation", project(":core:common"))
                add("implementation", project(":core:design-system"))
                add("implementation", project(":core:ui"))

                add("implementation", libs.findBundle("androidx.core.dependencies").get())
                add("implementation", libs.findBundle("androidx.lifecycle.dependencies").get())
                add("implementation", libs.findBundle("android.responsive.size.dependencies").get())
                add("implementation", libs.findBundle("androidx.navigation.dependencies").get())

                add("implementation", libs.findLibrary("timber").get())
                add("implementation", libs.findLibrary("gson").get())

                add("implementation", libs.findLibrary("kotlin.coroutines").get())

                add("testImplementation", libs.findLibrary("test.junit").get())
                add("androidTestImplementation", libs.findLibrary("test.extjunit").get())
                add( "androidTestImplementation",libs.findLibrary("test.espresso").get())
                add( "androidTestImplementation",libs.findLibrary("test.core").get())
                add( "androidTestImplementation",libs.findLibrary("test.runner").get())
            }
        }
    }
}
