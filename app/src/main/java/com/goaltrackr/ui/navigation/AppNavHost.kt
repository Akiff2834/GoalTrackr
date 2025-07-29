package com.goaltrackr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.goaltrackr.ui.navigation.Screen
import com.goaltrackr.ui.screens.GoalDetailScreen
import com.goaltrackr.ui.screens.HomeScreen
import com.goaltrackr.ui.screens.LoginScreen
import com.goaltrackr.ui.screens.ProfileScreen
import com.goaltrackr.ui.screens.SettingsScreen
import com.goaltrackr.ui.screens.SplashScreen
import com.goaltrackr.ui.screens.TodoListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onDeleteAllGoals: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen { isLoggedIn ->
                if (isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                navController = navController,
                onDeleteAllGoals = onDeleteAllGoals
            )
        }

        composable(
            route = "goalDetail/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            GoalDetailScreen(goalId = goalId, onBack = { navController.popBackStack() })
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TodoList.route) {
            TodoListScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onDeleteAllGoals = onDeleteAllGoals,
                navController = navController // 👈 gerekli
            )
        }
    }
}
