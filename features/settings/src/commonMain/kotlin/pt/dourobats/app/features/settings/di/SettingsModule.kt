package pt.dourobats.app.features.settings.di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import pt.dourobats.app.features.settings.SettingsViewModel

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
