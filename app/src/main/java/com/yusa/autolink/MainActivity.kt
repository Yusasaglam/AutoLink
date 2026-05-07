package com.yusa.autolink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yusa.autolink.navigation.AppNavigation
import com.yusa.autolink.ui.theme.AutoLinkTheme

// Uygulamanın başlangıç noktası - tek Activity kullanıyoruz (Compose mimarisi)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoLinkTheme {
                // Tüm ekranlar AppNavigation üzerinden yönetilir
                AppNavigation()
            }
        }
    }
}
