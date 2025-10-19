package com.thuliotech.missed

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MissedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

