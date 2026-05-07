package com.yusa.autolink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yusa.autolink.ui.screens.auth.LoginScreen
import com.yusa.autolink.ui.screens.auth.RegisterScreen
import com.yusa.autolink.ui.screens.dashboard.DashboardScreen
import com.yusa.autolink.ui.screens.listing.ListingScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Dashboard : Screen("dashboard")
    data object Listing : Screen("listing")
}

@Composable
fun AutoLinkNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToListing = { navController.navigate(Screen.Listing.route) }
            )
        }
        composable(Screen.Listing.route) {
            ListingScreen(
                onNavigateToDashboard = { navController.popBackStack() }
            )
        }
    }
}
