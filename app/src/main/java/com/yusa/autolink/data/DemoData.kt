package com.yusa.autolink.data

import com.yusa.autolink.data.model.*

// Tüm demo/mock veriler burada tutulur.
// Gerçek uygulamada bunlar backend API'sinden gelirdi.
object DemoData {

    // Demo kullanıcı
    val currentUser = User(
        name  = "Ahmet Yılmaz",
        phone = "0532 123 45 67",
        email = "ahmet.yilmaz@gmail.com"
    )

    // Kullanıcının kayıtlı aracı
    val userVehicle = Vehicle(
        id       = 1,
        brand    = "Renault",
        model    = "Clio 4",
        year     = 2012,
        plate    = "34 ABC 123",
        fuelType = "Dizel",
        engine   = "1.5 dCi"
    )

    // Onboarding sayfaları (aktif navigasyonda kullanılmıyor, dosya derlensin diye tutuldu)
    val onboardingPages = listOf(
        OnboardingPage("Şeffaf Fiyatlandırma", "Tüm hizmet bedelleri önceden gösterilir.", "price_check"),
        OnboardingPage("Aracınıza Uygun Servisler", "Marka/modele göre en uygun seçenekler.", "directions_car"),
        OnboardingPage("Randevu ve Hizmet Takibi", "Kolayca randevu oluşturun.", "calendar_today")
    )

    // Hizmet kategorileri (ServiceDetailScreen için geriye dönük uyumluluk)
    val services = listOf(
        Service(1, "Araba Yıkama", "Detaylı iç ve dış yıkama.", 300, "45-60 dk", "wash"),
        Service(2, "Oto Bakım",    "Kapsamlı periyodik bakım.", 1500, "2-3 saat", "build")
    )

    // ── ARABA YIKAMA İŞLETMELERİ ──────────────────────────────────────
    // ── OTO BAKIM İŞLETMELERİ ─────────────────────────────────────────
    val businesses = listOf(

        Business(
            id            = 1,
            name          = "Star Oto Kuaför",
            rating        = 4.8f,
            distanceKm    = 1.2f,
            distanceText  = "1.2 km",
            startingPrice = 280,
            isVerified    = true,
            address       = "Atatürk Cad. No:45, Bağcılar / İstanbul",
            hasValet      = true,
            onSiteService = false,
            isAvailable   = true,
            type          = BusinessType.WASHING
        ),
        Business(
            id            = 2,
            name          = "Hızlı Yıkama Merkezi",
            rating        = 4.3f,
            distanceKm    = 0.8f,
            distanceText  = "800 m",
            startingPrice = 200,
            isVerified    = false,
            address       = "Sanayi Cad. No:12, Esenler / İstanbul",
            hasValet      = false,
            onSiteService = true,
            isAvailable   = true,
            type          = BusinessType.WASHING
        ),
        Business(
            id            = 3,
            name          = "Premium Oto Spa",
            rating        = 4.9f,
            distanceKm    = 2.5f,
            distanceText  = "2.5 km",
            startingPrice = 450,
            isVerified    = true,
            address       = "Bağlar Mah. No:8, Güneşli / İstanbul",
            hasValet      = true,
            onSiteService = false,
            isAvailable   = false,
            type          = BusinessType.WASHING
        ),
        Business(
            id            = 4,
            name          = "Mobil Yıkama Pro",
            rating        = 4.5f,
            distanceKm    = 1.8f,
            distanceText  = "1.8 km",
            startingPrice = 320,
            isVerified    = true,
            address       = "İkitelli OSB, Küçükçekmece / İstanbul",
            hasValet      = false,
            onSiteService = true,
            isAvailable   = true,
            type          = BusinessType.WASHING
        ),

        Business(
            id            = 5,
            name          = "Güven Oto Bakım Merkezi",
            rating        = 4.6f,
            distanceKm    = 2.5f,
            distanceText  = "2.5 km",
            startingPrice = 1650,
            isVerified    = true,
            address       = "Yenimahalle Cad. No:12, Güneşli / İstanbul",
            hasValet      = false,
            onSiteService = false,
            isAvailable   = true,
            type          = BusinessType.MAINTENANCE
        ),
        Business(
            id            = 6,
            name          = "Pro Teknik Servis",
            rating        = 4.9f,
            distanceKm    = 3.1f,
            distanceText  = "3.1 km",
            startingPrice = 1200,
            isVerified    = true,
            address       = "Organize Sanayi Bölgesi No:5, İkitelli / İstanbul",
            hasValet      = false,
            onSiteService = false,
            isAvailable   = false,
            type          = BusinessType.MAINTENANCE
        ),
        Business(
            id            = 7,
            name          = "Hızlı Lastik & Rot Balans",
            rating        = 4.4f,
            distanceKm    = 1.5f,
            distanceText  = "1.5 km",
            startingPrice = 500,
            isVerified    = false,
            address       = "Çırpıcı Cad. No:23, Zeytinburnu / İstanbul",
            hasValet      = false,
            onSiteService = false,
            isAvailable   = true,
            type          = BusinessType.MAINTENANCE
        )
    )

    // Demo randevular - 3 farklı durum: Tamamlandı, Onaylandı, Beklemede
    val appointments = listOf(
        Appointment(
            id           = 1,
            businessName = "Star Oto Kuaför",
            serviceName  = "Araba Yıkama",
            date         = "12 Mayıs 2026",
            time         = "10:30",
            totalPrice   = 320,
            vehicleName  = "Renault Clio 4 2012",
            status       = AppointmentStatus.COMPLETED
        ),
        Appointment(
            id           = 2,
            businessName = "Güven Oto Bakım Merkezi",
            serviceName  = "Oto Bakım",
            date         = "20 Mayıs 2026",
            time         = "14:00",
            totalPrice   = 1850,
            vehicleName  = "Renault Clio 4 2012",
            status       = AppointmentStatus.CONFIRMED
        ),
        Appointment(
            id           = 3,
            businessName = "Mobil Yıkama Pro",
            serviceName  = "Araba Yıkama",
            date         = "25 Mayıs 2026",
            time         = "09:00",
            totalPrice   = 320,
            vehicleName  = "Renault Clio 4 2012",
            status       = AppointmentStatus.PENDING
        )
    )

    // Randevu ekranı tarih/saat seçenekleri
    val availableDates = listOf(
        "12 Mayıs 2026", "13 Mayıs 2026", "14 Mayıs 2026",
        "15 Mayıs 2026", "16 Mayıs 2026", "19 Mayıs 2026", "20 Mayıs 2026"
    )
    val availableTimes = listOf(
        "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "13:00", "13:30",
        "14:00", "14:30", "15:00", "15:30"
    )
}
