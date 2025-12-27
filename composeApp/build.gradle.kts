plugins {
    id("pt.dourobats.app.android.application")
    id("pt.dourobats.app.kmp")
    id("pt.dourobats.app.compose")
}

android {
    namespace = "pt.dourobats.app"

    defaultConfig {
        applicationId = "pt.dourobats.app"
        versionCode = 1
        versionName = "1.0"
    }
}

