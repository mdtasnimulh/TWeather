import com.tasnimulhasan.tweather.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidNavigationComponentConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.navigation.safeargs.kotlin")

            dependencies {
                add("implementation", libs.findBundle("androidx.navigation.dependencies").get())
            }
        }
    }
}