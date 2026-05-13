package com.mark.shereheke.models

data class Wine(
    val name: String = "",
    val imageUrl: String = ""
)

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
    val wines: List<Wine> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

val sampleEvents = listOf(
    Event(
        id = "1",
        title = "Nairobi Jazz Festival",
        description = "Experience an unforgettable night of smooth jazz under the stars at the heart of Nairobi. Featuring top local and international jazz artists, gourmet food, and a selection of fine wines.",
        date = "OCT 24, 2024",
        time = "18:00",
        venue = "The Alchemist",
        city = "Nairobi",
        category = "Jazz Night",
        ticketPrice = "3500",
        totalTickets = "200",
        imageUrl = "https://images.unsplash.com/photo-1514525253361-bee1a1bb7380"
    ),
    Event(
        id = "2",
        title = "Rooftop Summer Vibe",
        description = "Kick off the summer with the ultimate rooftop party. Enjoy panoramic city views, live DJ sets, and signature cocktails. The perfect way to spend your Saturday evening.",
        date = "NOV 12, 2024",
        time = "16:00",
        venue = "The View Rooftop",
        city = "Mombasa",
        category = "Rooftop",
        ticketPrice = "2000",
        totalTickets = "150",
        imageUrl = "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3"
    ),
    Event(
        id = "3",
        title = "Annual Charity Gala",
        description = "Join us for a night of elegance and philanthropy at our Annual Charity Gala. All proceeds go towards supporting local education initiatives. Black-tie attire required.",
        date = "DEC 05, 2024",
        time = "19:00",
        venue = "Villa Rosa Kempinski",
        city = "Nairobi",
        category = "Gala",
        ticketPrice = "10000",
        totalTickets = "100",
        imageUrl = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622"
    )
)
