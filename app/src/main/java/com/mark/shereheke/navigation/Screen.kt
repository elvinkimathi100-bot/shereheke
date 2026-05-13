package com.mark.shereheke.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object EventDetail : Screen("eventDetail/{eventId}") {
        fun createRoute(eventId: String) = "eventDetail/$eventId"
    }
    object Checkout : Screen("checkout/{eventId}") {
        fun createRoute(eventId: String) = "checkout/$eventId"
    }
    object Payment : Screen("payment/{eventId}/{quantity}") {
        fun createRoute(eventId: String, quantity: Int) = "payment/$eventId/$quantity"
    }
    object MyTickets : Screen("myTickets")
    object HotelDashboard : Screen("hotelDashboard")
    object CreateEvent : Screen("create_event")
}
