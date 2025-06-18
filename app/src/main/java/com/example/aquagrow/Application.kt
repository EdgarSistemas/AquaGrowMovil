package com.example.aquagrow

import android.app.Application
import com.example.aquagrow.core.network.TokenManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.initialize(this)
    }
}