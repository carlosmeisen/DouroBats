plugins {
    id("pt.dourobats.app.android.library")
    id("pt.dourobats.app.kmp")
    id("pt.dourobats.app.compose")
}

compose.resources {
    publicResClass = true
}

android {
    namespace = "pt.dourobats.app.core.ui"
}
