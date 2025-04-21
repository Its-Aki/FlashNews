package com.aki.flashnews

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {
    // Optional: Initialize libraries like Timber for logging here
    override fun onCreate() {
        super.onCreate()
        // Example: if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}