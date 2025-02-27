package com.example.castvideo

import android.app.Application
import android.util.Log
import com.google.android.gms.cast.framework.CastContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            CastContext.getSharedInstance(this)
        } catch (e: RuntimeException) {
            Log.e("CastError", "Failed to initialize CastContext: ${e.message}")
        }
    }
}
