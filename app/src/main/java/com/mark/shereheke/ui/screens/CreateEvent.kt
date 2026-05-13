package com.mark.shereheke.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.mark.shereheke.data.EventViewModel
import com.mark.shereheke.models.Event

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEventScreen(navController: NavController, eventViewModel: EventViewModel = viewModel()) {

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
    var showSuccess by remember { mutableStateOf(false) }

    // Wine Selection State
    var wineList by remember { mutableStateOf(listOf<Pair<String, Uri>>()) }
    var currentWineName by remember { mutableStateOf("") }
    var currentWineUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val categories = listOf(
        "Jazz Night", "Music", "Food & Drink", "Arts", "Sports",
        "Networking", "Comedy", "Fashion", "Tech", "Rooftop", "Gala"
    )

    val bannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val wineImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> currentWineUri = uri }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    .clickable { bannerPicker.launch("image/*") },
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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            // Jazz Night Specific: Wine Upload Section
            if (selectedCategory == "Jazz Night" || selectedCategory == "Music" || selectedCategory == "Food & Drink") {
                SectionLabel(icon = "🍷", title = "Wine Selection (Jazz Night Specials)")
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = currentWineName,
                            onValueChange = { currentWineName = it },
                            label = { Text("Wine Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Gray.copy(alpha = 0.2f))
                                    .clickable { wineImagePicker.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (currentWineUri != null) {
                                    AsyncImage(model = currentWineUri, contentDescription = null, contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null)
                                }
                            }
                            
                            Button(
                                onClick = {
                                    if (currentWineName.isNotBlank() && currentWineUri != null) {
                                        wineList = wineList + (currentWineName to currentWineUri!!)
                                        currentWineName = ""
                                        currentWineUri = null
                                    } else {
                                        Toast.makeText(context, "Enter wine name and select image", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add Wine")
                            }
                        }
                    }
                }

                // Display added wines
                wineList.forEachIndexed { index, wine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = wine.second,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(wine.first, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { wineList = wineList.toMutableList().apply { removeAt(index) } }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
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
                    if (imageUri == null) {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (eventTitle.isBlank() || eventVenue.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val event = Event(
                        title = eventTitle,
                        description = eventDescription,
                        date = eventDate,
                        time = eventTime,
                        venue = eventVenue,
                        city = eventCity,
                        category = selectedCategory,
                        ticketPrice = ticketPrice,
                        totalTickets = totalTickets
                    )

                    eventViewModel.uploadEventWithWines(
                        context = context,
                        bannerUri = imageUri!!,
                        wineUris = wineList,
                        event = event,
                        onSuccess = {
                            showSuccess = true
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = !eventViewModel.isUploading
            ) {
                if (eventViewModel.isUploading) {
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

@Composable
fun SectionLabel(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EventTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(rememberNavController())
}
