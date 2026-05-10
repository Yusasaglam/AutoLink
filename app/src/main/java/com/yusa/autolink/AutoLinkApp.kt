package com.yusa.autolink

import android.app.Application
import com.yusa.autolink.data.AppState

class AutoLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppState.load(this)
    }
}
