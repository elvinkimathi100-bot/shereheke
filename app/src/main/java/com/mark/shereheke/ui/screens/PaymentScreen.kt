package com.mark.shereheke.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mark.shereheke.data.EventViewModel
import com.mark.shereheke.data.TicketViewModel
import com.mark.shereheke.models.Ticket
import com.mark.shereheke.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    eventId: String?,
    quantity: Int,
    eventViewModel: EventViewModel = viewModel(),
    ticketViewModel: TicketViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val event = eventViewModel.events.find { it.id == eventId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M-Pesa Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Pay via M-Pesa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            event?.let {
                Text(text = "Booking ${it.title}", fontWeight = FontWeight.Medium)
                Text(text = "Quantity: $quantity", style = MaterialTheme.typography.bodySmall)
                val total = (it.ticketPrice.toDoubleOrNull() ?: 0.0) * quantity
                Text(text = "Total: KES $total", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Enter your M-Pesa phone number to receive a payment prompt.")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number (e.g., 0712...)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            if (ticketViewModel.isProcessing) {
                CircularProgressIndicator()
                Text(text = "Processing Payment...", modifier = Modifier.padding(top = 8.dp))
            } else {
                Button(
                    onClick = {
                        if (phoneNumber.length < 10) {
                            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Here you would normally trigger the STK Prompt
                        Toast.makeText(context, "STK Prompt Sent", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pay Now")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // "Simulate Success" logic to actually add the ticket to database
            TextButton(
                onClick = {
                    if (event == null) return@TextButton
                    
                    val newTicket = Ticket(
                        eventId = event.id,
                        eventTitle = event.title,
                        date = event.date,
                        location = "${event.venue}, ${event.city}",
                        price = event.ticketPrice.toDoubleOrNull() ?: 0.0,
                        quantity = quantity,
                        status = "Confirmed"
                    )
                    
                    ticketViewModel.bookTicket(
                        ticket = newTicket,
                        onSuccess = {
                            Toast.makeText(context, "Ticket Booked Successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.MyTickets.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !ticketViewModel.isProcessing
            ) {
                Text("Simulate Success")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen(rememberNavController(), "1", 1)
}
