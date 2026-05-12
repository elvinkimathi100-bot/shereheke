package com.mark.shereheke.navigation

const val ROUTE_SPLASH          = "splash"
const val ROUTE_LOGIN           = "login"
const val ROUTE_SIGNUP          = "signup"
const val ROUTE_HOME            = "home"
const val ROUTE_DASHBOARD       = "dashboard"
const val ROUTE_HOTEL_DASHBOARD = "hotel_dashboard"
const val ROUTE_CHECKOUT     = "checkout"
const val ROUTE_PAYMENT    = "payment"
const val ROUTE_UPDATE_EVENT    = "update_event/{eventId}"
const val ROUTE_CREATE_EVENT       = "create_event"
const val ROUTE_MY_TICKETS   = "event_detail/{eventId}"
const val ROUTE_EVENT_DETAIL    = "event_detail/{eventId}"

fun updateEventRoute(eventId: String) = "update_event/$eventId"
fun eventDetailRoute(eventId: String) = "event_detail/$eventId"