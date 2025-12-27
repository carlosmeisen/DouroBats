plugins {
    `kotlin-dsl`
}

group = "pt.dourobats.app.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "pt.dourobats.app.kmp"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("androidApplication") {
            id = "pt.dourobats.app.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "pt.dourobats.app.compose"
            implementationClass = "ComposeMultiplatformConventionPlugin"
        }
    }
}
