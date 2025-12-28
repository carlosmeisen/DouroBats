package pt.dourobats.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dourobats.core.ui.generated.resources.Res
import dourobats.core.ui.generated.resources.login_button_temp
import dourobats.core.ui.generated.resources.login_title
import dourobats.core.ui.generated.resources.nav_home
import dourobats.core.ui.generated.resources.nav_settings
import dourobats.core.ui.generated.resources.nav_training
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pt.dourobats.app.core.ui.theme.AppTheme
import pt.dourobats.app.features.home.HomeScreen
import pt.dourobats.app.features.schedule.ScheduleScreen
import pt.dourobats.app.features.settings.SettingsScreen

@Composable
@Preview
fun App() {
    AppTheme {
        // TODO: Implement proper authentication flow with splash screen
        // For now, we'll show the main app directly
        var isAuthenticated by remember { mutableStateOf(true) }

        if (isAuthenticated) {
            MainApp()
        } else {
            LoginScreen(onLoginSuccess = { isAuthenticated = true })
        }
    }
}

@Composable
private fun MainApp() {
    var selectedScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        icon = { Text(screen.emoji) },
                        label = { Text(stringResource(screen.titleRes)) },
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedScreen) {
                Screen.Home -> HomeScreen()
                Screen.Training -> ScheduleScreen()
                Screen.Settings -> SettingsScreen()
            }
        }
    }
}

@Composable
private fun LoginScreen(onLoginSuccess: () -> Unit) {
    // TODO: Implement proper login UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.login_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginSuccess) {
            Text(stringResource(Res.string.login_button_temp))
        }
    }
}

private enum class Screen(val titleRes: org.jetbrains.compose.resources.StringResource, val emoji: String) {
    Home(Res.string.nav_home, "\uD83C\uDFE0"),
    Training(Res.string.nav_training, "\uD83C\uDFD0"),
    Settings(Res.string.nav_settings, "⚙️")
}