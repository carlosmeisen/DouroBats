package pt.dourobats.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pt.dourobats.app.core.data.di.dataModule
import pt.dourobats.app.core.data.preferences.initDataStore
import pt.dourobats.app.features.settings.di.settingsModule

class DouroBatsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize DataStore
        initDataStore(this)

        // Initialize Koin
        startKoin {
            androidContext(this@DouroBatsApplication)
            modules(
                dataModule,
                settingsModule
            )
        }
    }
}
