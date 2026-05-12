package com.mark.shereheke.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mark.shereheke.models.Event

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEventScreen(navController: NavController) {

    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventVenue by remember { mutableStateOf("") }
    var eventCity by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    var totalTickets by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val categories = listOf(
        "Music", "Food & Drink", "Arts", "Sports",
        "Networking", "Comedy", "Fashion", "Tech"
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val scrollState = rememberScrollState()

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            icon = { Text("🎉", fontSize = 32.sp) },
            title = { Text("Event Created!", fontWeight = FontWeight.Bold) },
            text = { Text("Your event has been successfully submitted and is pending review.") },
            confirmButton = {
                Button(onClick = {
                    showSuccess = false
                    navController.popBackStack()
                }) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Create Event", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text(
                            "Fill in the details below",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            SectionLabel(icon = "🖼️", title = "Event Banner")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        2.dp,
                        if (imageUri != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Text("Tap to upload banner image")
                    }
                }
            }

            SectionLabel(icon = "📋", title = "Basic Info")

            EventTextField(eventTitle, { eventTitle = it }, "Event Title", "e.g. Nairobi Jazz Night", Icons.Default.Star)
            EventTextField(eventDescription, { eventDescription = it }, "Description", "Tell people what to expect...", Icons.Default.Description)

            Text("Category")
            FlowRow {
                categories.forEach {
                    FilterChip(
                        selected = selectedCategory == it,
                        onClick = { selectedCategory = it },
                        label = { Text(it) }
                    )
                }
            }

            SectionLabel(icon = "📅", title = "Date & Time")

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EventTextField(eventDate, { eventDate = it }, "Date", "DD/MM/YYYY", Icons.Default.CalendarToday, Modifier.weight(1f))
                EventTextField(eventTime, { eventTime = it }, "Time", "7:00 PM", Icons.Default.Schedule, Modifier.weight(1f))
            }

            SectionLabel(icon = "📍", title = "Location")

            EventTextField(eventVenue, { eventVenue = it }, "Venue", "e.g. Serena Hotel", Icons.Default.LocationOn)
            EventTextField(eventCity, { eventCity = it }, "City", "e.g. Nairobi", Icons.Default.LocationCity)

            SectionLabel(icon = "🎟️", title = "Tickets")

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EventTextField(ticketPrice, { ticketPrice = it }, "Price", "KES 2500", Icons.Default.AttachMoney, Modifier.weight(1f))
                EventTextField(totalTickets, { totalTickets = it }, "Tickets", "200", Icons.Default.ConfirmationNumber, Modifier.weight(1f))
            }

            Button(
                onClick = {

                    isLoading = true

                    val database = FirebaseDatabase.getInstance().reference
                    val storage = FirebaseStorage.getInstance().reference

                    val eventId = database.child("events").push().key ?: return@Button

                    fun saveEvent(imageUrl: String) {
                        val event = Event(
                            id = eventId,
                            title = eventTitle,
                            description = eventDescription,
                            date = eventDate,
                            time = eventTime,
                            venue = eventVenue,
                            city = eventCity,
                            category = selectedCategory,
                            ticketPrice = ticketPrice,
                            totalTickets = totalTickets,
                            imageUrl = imageUrl
                        )

                        database.child("events").child(eventId)
                            .setValue(event)
                            .addOnSuccessListener {
                                isLoading = false
                                showSuccess = true
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    }

                    if (imageUri != null) {

                        val imageRef = storage.child("event_images/$eventId.jpg")

                        imageRef.putFile(imageUri!!)
                            .continueWithTask { imageRef.downloadUrl }
                            .addOnSuccessListener { uri ->
                                saveEvent(uri.toString())
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }

                    } else {
                        saveEvent("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Publish Event")
                }
            }
        }
    }
}