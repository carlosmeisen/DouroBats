package pt.dourobats.app.core.ui.localization

import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import pt.dourobats.app.core.domain.model.Language
import java.util.Locale

@Composable
actual fun ProvideLocalizedContext(
    language: Language,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val locale = Locale.forLanguageTag(language.bcp47Tag)

    val configuration = Configuration(context.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocales(LocaleList(locale))
    } else {
        @Suppress("DEPRECATION")
        configuration.locale = locale
    }

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLanguage provides language
    ) {
        content()
    }
}
