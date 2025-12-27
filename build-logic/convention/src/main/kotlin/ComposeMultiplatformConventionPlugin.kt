import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.apply {
                    commonMain {
                        dependencies {
                            implementation(compose.runtime)
                            implementation(compose.foundation)
                            implementation(compose.material3)
                            implementation(compose.ui)
                            implementation(compose.components.resources)
                            implementation(compose.components.uiToolingPreview)
                            implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                            implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                        }
                    }
                    androidMain {
                        dependencies {
                            implementation(compose.preview)
                            implementation(libs.findLibrary("androidx-activity-compose").get())
                        }
                    }
                    commonTest {
                        dependencies {
                            implementation(libs.findLibrary("kotlin-test").get())
                        }
                    }
                }
            }

            dependencies {
                add("debugImplementation", compose.uiTooling)
            }
        }
    }

    private val Project.compose
        get() = extensions.getByName("compose") as org.jetbrains.compose.ComposePlugin.Dependencies

    private val Project.libs
        get() = extensions.getByName("libs") as org.gradle.accessors.dm.LibrariesForLibs
}
