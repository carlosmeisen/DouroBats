package pt.dourobats.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview
import pt.dourobats.app.features.home.HomeScreen
import pt.dourobats.app.features.schedule.ScheduleScreen
import pt.dourobats.app.features.settings.SettingsScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
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
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
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
            text = "DouroBats Login",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginSuccess) {
            Text("Login (Temporary)")
        }
    }
}

private enum class Screen(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Training("Training", Icons.Default.CalendarMonth),
    Settings("Settings", Icons.Default.Settings)
}