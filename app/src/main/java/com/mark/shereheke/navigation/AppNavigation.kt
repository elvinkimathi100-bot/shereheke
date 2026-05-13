package com.mark.shereheke.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController


import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mark.shereheke.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.HotelDashboard.route) { HotelDashboardScreen(navController) }
        composable(Screen.MyTickets.route) { MyTicketsScreen(navController) }
        composable(Screen.CreateEvent.route) { AddEventScreen(navController) }

        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EventDetailScreen(navController, eventId)
        }

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            CheckoutScreen(navController, eventId)
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("quantity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1
            PaymentScreen(navController, eventId, quantity)
        }
    }
}