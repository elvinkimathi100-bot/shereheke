package com.mark.shereheke.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.*
import com.mark.shereheke.models.Event

class EventViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("events")

    // Observed list of all events
    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    // Upload state for UI feedback
    var isUploading by mutableStateOf(false)
        private set

    init {
        fetchEvents()
    }

    // ─────────────────────────────────────────────
    // FETCH – real-time listener
    // ─────────────────────────────────────────────
    private fun fetchEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                events = snapshot.children.mapNotNull {
                    it.getValue(Event::class.java)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventViewModel", "Fetch error: ${error.message}")
            }
        })
    }

    // ─────────────────────────────────────────────
    // ADD EVENT
    // ─────────────────────────────────────────────
    fun addEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val eventId = database.push().key ?: return onError("Could not generate event ID")
        val finalEvent = event.copy(id = eventId)

        database.child(eventId).setValue(finalEvent)
            .addOnSuccessListener {
                Log.d("EventViewModel", "Event added: $eventId")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("EventViewModel", it.message ?: "Unknown error")
                onError(it.message ?: "Failed to add event")
            }
    }

    // ─────────────────────────────────────────────
    // UPLOAD EVENT BANNER / IMAGE TO CLOUDINARY
    // ─────────────────────────────────────────────
    fun uploadImageAndAddEvent(
        context: Context,
        imageUri: Uri,
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isUploading = true
        
        MediaManager.get().upload(imageUri)
            .option("folder", "events")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary", "Uploading: $bytes / $totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String
                    if (imageUrl != null) {
                        val finalEvent = event.copy(imageUrl = imageUrl)
                        addEvent(finalEvent, {
                            isUploading = false
                            onSuccess()
                        }, { error ->
                            isUploading = false
                            onError(error)
                        })
                    } else {
                        isUploading = false
                        onError("Failed to get image URL from Cloudinary")
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    isUploading = false
                    onError(error?.description ?: "Cloudinary upload error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    isUploading = false
                    onError("Upload rescheduled: ${error?.description}")
                }
            }).dispatch()
    }
}
