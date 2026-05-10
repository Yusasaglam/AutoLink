package com.yusa.autolink

import android.app.Application
import com.yusa.autolink.data.AppState

// Application sınıfı — uygulama ilk açıldığında bir kez çalışır.
// AndroidManifest.xml'de "android:name" ile kayıtlıdır.
class AutoLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Uygulama başlar başlamaz kayıtlı kullanıcı verilerini ve oturumu yükle.
        // AppState.load() çağrılmazsa SharedPreferences'tan veri okunamaz.
        AppState.load(this)
    }
}
