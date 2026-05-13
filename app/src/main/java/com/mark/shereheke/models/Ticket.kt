package com.mark.shereheke.models

data class Ticket(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val eventTitle: String = "",
    val date: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val status: String = "" // e.g., "Paid", "Pending"
)
