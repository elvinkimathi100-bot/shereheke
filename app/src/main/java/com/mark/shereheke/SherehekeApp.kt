package com.mark.shereheke

import android.app.Application
import com.cloudinary.android.MediaManager

class SherehekeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "dw4drxbto",
            "api_key" to "357562845161859",
            "api_secret" to "GETpyfqdFJMYzNuEE0ymmIUnywc",
        )
        MediaManager.init(this, config)
    }
}
