package com.yusa.autolink.data

// Demo uygulamanın basit global durumu.
// Gerçek uygulamada ViewModel veya DataStore kullanılırdı.
object AppState {
    var isLoggedIn:  Boolean = false  // Demo: giriş yapıldı mı?
    var selectedTab: Int     = 0      // Alt menü sekmesi: 0=Ana Sayfa, 1=Randevularım, 2=Araçlarım, 3=Profil

    // Son oluşturulan randevunun bilgileri — başarı ekranına taşınır
    var lastBusinessName: String = ""
    var lastServiceName:  String = ""
    var lastDate:         String = ""
    var lastTime:         String = ""
    var lastPrice:        Int    = 0
}
