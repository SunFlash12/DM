package com.dungeonmaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.dungeonmaster.app.ui.AppNavigation
import com.dungeonmaster.app.ui.theme.AIDungeonMasterTheme
import com.dungeonmaster.app.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.state.collectAsState()
            var darkTheme by remember { mutableStateOf(true) }

            LaunchedEffect(settingsState.darkTheme) {
                darkTheme = settingsState.darkTheme
            }

            AIDungeonMasterTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        darkTheme = darkTheme,
                        onThemeChange = { newTheme ->
                            darkTheme = newTheme
                            settingsViewModel.setDarkTheme(newTheme)
                        }
                    )
                }
            }
        }
    }
}
