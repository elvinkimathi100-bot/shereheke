package com.mark.shereheke.models

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val venue: String = "",
    val city: String = "",
    val category: String = "",
    val ticketPrice: String = "",
    val totalTickets: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)