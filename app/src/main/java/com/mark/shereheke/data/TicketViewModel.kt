package com.mark.shereheke.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mark.shereheke.models.Ticket

class TicketViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("tickets")
    private val auth = FirebaseAuth.getInstance()

    var tickets by mutableStateOf<List<Ticket>>(emptyList())
        private set

    var isProcessing by mutableStateOf(false)
        private set

    init {
        fetchUserTickets()
    }

    fun fetchUserTickets() {
        val userId = auth.currentUser?.uid ?: return
        database.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tickets = snapshot.children.mapNotNull {
                        it.getValue(Ticket::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TicketViewModel", "Fetch error: ${error.message}")
                }
            })
    }

    fun bookTicket(
        ticket: Ticket,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isProcessing = true
        val ticketId = database.push().key ?: return onError("Could not generate ticket ID")
        val finalTicket = ticket.copy(id = ticketId, userId = auth.currentUser?.uid ?: "")

        database.child(ticketId).setValue(finalTicket)
            .addOnSuccessListener {
                isProcessing = false
                onSuccess()
            }
            .addOnFailureListener {
                isProcessing = false
                onError(it.message ?: "Failed to book ticket")
            }
    }
}
