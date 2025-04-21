package com.aki.flashnews.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aki.flashnews.presentation.screens.NewsDetailScreen
import com.aki.flashnews.presentation.screens.NewsListScreen

sealed class Screen(val route: String) {
    object NewsList : Screen("news_list")
    object NewsDetail : Screen("news_detail/{articleUrl}") { // Argument defined in route
        fun createRoute(articleUrl: String) = "news_detail/$articleUrl" // Helper function
    }
    // Add other screens like Settings, Search etc. here
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.NewsList.route // Define the starting screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // News List Screen
        composable(route = Screen.NewsList.route) {
            NewsListScreen(
                onArticleClick = { encodedUrl ->
                    // Navigate to detail screen, passing the encoded URL
                    navController.navigate(Screen.NewsDetail.createRoute(encodedUrl))
                }
            )
        }

        // News Detail Screen
        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(navArgument("articleUrl") { // Define the argument
                type = NavType.StringType
                // nullable = false // Default, URL should not be null here
            })
        ) {
            // ViewModel will automatically receive the 'articleUrl' via SavedStateHandle
            NewsDetailScreen(
                onNavigateBack = { navController.popBackStack() } // Simple back navigation
            )
        }

        // Add other destinations here (e.g., composable(Screen.Settings.route) { ... })
    }
}