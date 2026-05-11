package com.yusa.autolink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yusa.autolink.navigation.AppNavigation
import com.yusa.autolink.ui.theme.AutoLinkTheme

// ============================================================
// MainActivity — Uygulamanın tek Activity'si
//
// Android'de her uygulama en az bir Activity ile başlar.
// Jetpack Compose mimarisinde tek bir Activity yeterlidir;
// ekranlar arası geçiş AppNavigation ile yapılır, ayrı
// Activity açılmaz. Bu sayede bellek kullanımı azalır ve
// animasyonlar daha akıcı çalışır.
// ============================================================
class MainActivity : ComponentActivity() {

    // onCreate → Activity ilk oluşturulduğunda Android tarafından çağrılır.
    // super.onCreate() → Android'in kendi başlangıç işlemlerini tamamlar (zorunlu).
    // enableEdgeToEdge() → İçeriğin durum çubuğu (status bar) arkasına kadar uzamasını sağlar.
    // setContent { } → XML layout yerine Compose UI'ı başlatır.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Tüm ekranlara Material 3 renk/yazı tipini uygular
            AutoLinkTheme {
                // Tüm ekranlar ve geçişler AppNavigation içinde tanımlıdır
                AppNavigation()
            }
        }
    }
}
