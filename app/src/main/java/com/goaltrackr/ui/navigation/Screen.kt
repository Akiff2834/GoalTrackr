package com.goaltrackr.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object GoalDetail : Screen("goal_detail/{goalId}") {
        fun createRoute(goalId: String) = "goal_detail/$goalId"
    }
    object Settings : Screen("settings")
    object TodoList : Screen("publicTodos") // ✅ Burası
}
