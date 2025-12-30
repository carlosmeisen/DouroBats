package pt.dourobats.app.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Factory function to create DataStore instance.
 * Platform-specific implementations are provided in androidMain and iosMain.
 */
expect fun createDataStore(): DataStore<Preferences>
