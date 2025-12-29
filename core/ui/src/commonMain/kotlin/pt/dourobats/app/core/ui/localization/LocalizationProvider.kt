package pt.dourobats.app.core.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import pt.dourobats.app.core.domain.model.Language

/**
 * CompositionLocal for the current language.
 * This triggers recomposition when the language changes.
 */
val LocalLanguage = compositionLocalOf { Language.ENGLISH_US }

/**
 * Provider that sets up the localization context.
 * When the language changes, all child composables will recompose
 * and string resources will use the selected language.
 */
@Composable
fun LocalizationProvider(
    language: Language,
    content: @Composable () -> Unit
) {
    ProvideLocalizedContext(language, content)
}

/**
 * Platform-specific localized context provider.
 * This wraps the content with the proper locale configuration.
 */
@Composable
expect fun ProvideLocalizedContext(
    language: Language,
    content: @Composable () -> Unit
)
