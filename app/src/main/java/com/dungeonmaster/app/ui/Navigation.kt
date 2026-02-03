package com.dungeonmaster.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dungeonmaster.app.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CharacterCreation : Screen("character_creation")
    object CharacterList : Screen("character_list")
    object CharacterSheet : Screen("character_sheet/{characterId}") {
        fun createRoute(characterId: Long) = "character_sheet/$characterId"
    }
    object Game : Screen("game/{sessionId}") {
        fun createRoute(sessionId: Long) = "game/$sessionId"
    }
    object NewGame : Screen("new_game/{characterId}") {
        fun createRoute(characterId: Long) = "new_game/$characterId"
    }
    object Settings : Screen("settings")
    object DiceRoller : Screen("dice_roller")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCharacterCreation = {
                    navController.navigate(Screen.CharacterCreation.route)
                },
                onNavigateToCharacterList = {
                    navController.navigate(Screen.CharacterList.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDiceRoller = {
                    navController.navigate(Screen.DiceRoller.route)
                }
            )
        }

        composable(Screen.CharacterCreation.route) {
            CharacterCreationScreen(
                onCharacterCreated = { characterId ->
                    navController.navigate(Screen.NewGame.createRoute(characterId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CharacterList.route) {
            CharacterListScreen(
                onSelectCharacter = { characterId ->
                    navController.navigate(Screen.CharacterSheet.createRoute(characterId))
                },
                onCreateCharacter = {
                    navController.navigate(Screen.CharacterCreation.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CharacterSheet.route,
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getLong("characterId") ?: 0L
            CharacterSheetScreen(
                characterId = characterId,
                onStartAdventure = { sessionId ->
                    navController.navigate(Screen.Game.createRoute(sessionId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.NewGame.route,
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getLong("characterId") ?: 0L
            NewGameScreen(
                characterId = characterId,
                onGameStarted = { sessionId ->
                    navController.navigate(Screen.Game.createRoute(sessionId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            GameScreen(
                sessionId = sessionId,
                onNavigateToCharacterSheet = { characterId ->
                    navController.navigate(Screen.CharacterSheet.createRoute(characterId))
                },
                onNavigateToDiceRoller = {
                    navController.navigate(Screen.DiceRoller.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                darkTheme = darkTheme,
                onThemeChange = onThemeChange,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DiceRoller.route) {
            DiceRollerScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
