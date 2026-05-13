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
import com.mark.shereheke.models.Wine

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
    // UPLOAD EVENT BANNER AND WINES TO CLOUDINARY
    // ─────────────────────────────────────────────
    fun uploadEventWithWines(
        context: Context,
        bannerUri: Uri,
        wineUris: List<Pair<String, Uri>>, // List of (Wine Name, Image Uri)
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isUploading = true
        
        // 1. Upload Banner
        MediaManager.get().upload(bannerUri)
            .option("folder", "events")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val bannerUrl = resultData?.get("secure_url") as? String
                    if (bannerUrl != null) {
                        // 2. Upload Wine Images if any
                        if (wineUris.isEmpty()) {
                            addEvent(event.copy(imageUrl = bannerUrl), {
                                isUploading = false
                                onSuccess()
                            }, { error ->
                                isUploading = false
                                onError(error)
                            })
                        } else {
                            uploadWines(context, wineUris, bannerUrl, event, onSuccess, onError)
                        }
                    } else {
                        isUploading = false
                        onError("Failed to get banner URL")
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    isUploading = false
                    onError(error?.description ?: "Banner upload error")
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    private fun uploadWines(
        context: Context,
        wineUris: List<Pair<String, Uri>>,
        bannerUrl: String,
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uploadedWines = mutableListOf<Wine>()
        var uploadCount = 0

        wineUris.forEach { (wineName, uri) ->
            MediaManager.get().upload(uri)
                .option("folder", "wines")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val wineUrl = resultData?.get("secure_url") as? String
                        if (wineUrl != null) {
                            uploadedWines.add(Wine(name = wineName, imageUrl = wineUrl))
                        }
                        uploadCount++
                        if (uploadCount == wineUris.size) {
                            addEvent(event.copy(imageUrl = bannerUrl, wines = uploadedWines), {
                                isUploading = false
                                onSuccess()
                            }, { error ->
                                isUploading = false
                                onError(error)
                            })
                        }
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        // Even if one wine fails, we might want to continue or stop.
                        // For simplicity, let's stop on first error.
                        isUploading = false
                        onError("Wine upload failed: ${error?.description}")
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    // Keep the old one for backward compatibility if needed, or update it
    fun uploadImageAndAddEvent(
        context: Context,
        imageUri: Uri,
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        uploadEventWithWines(context, imageUri, emptyList(), event, onSuccess, onError)
    }
}
