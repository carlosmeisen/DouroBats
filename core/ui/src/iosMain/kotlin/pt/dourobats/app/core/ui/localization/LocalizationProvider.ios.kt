package pt.dourobats.app.core.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import pt.dourobats.app.core.domain.model.Language

@Composable
actual fun ProvideLocalizedContext(
    language: Language,
    content: @Composable () -> Unit
) {
    // iOS locale configuration
    // For iOS, Compose Multiplatform resources use the system locale
    // Runtime locale switching on iOS may require additional native configuration
    CompositionLocalProvider(
        LocalLanguage provides language
    ) {
        content()
    }
}
