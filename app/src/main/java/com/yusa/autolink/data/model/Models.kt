package com.yusa.autolink.data.model

// Kullanıcı hesap tipi: müşteri mi yoksa işletme mi?
enum class AccountType { CUSTOMER, BUSINESS }

// İşletme türü: araba yıkama veya oto bakım
enum class BusinessType { WASHING, MAINTENANCE }

// Randevu durumu: beklemede, onaylandı, tamamlandı, iptal edildi
enum class AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }

// Temel kullanıcı bilgisi (ad, telefon, e-posta)
data class User(val name: String, val phone: String, val email: String)

// Kullanıcının kayıtlı aracı
data class Vehicle(
    val id: Int,           // Benzersiz araç numarası
    val brand: String,     // Marka (ör. Renault, BMW)
    val model: String,     // Model (ör. Clio, X5)
    val year: Int,         // Model yılı
    val plate: String,     // Plaka (ör. 34 ABC 123)
    val fuelType: String,  // Yakıt tipi (Benzin, Dizel, LPG…)
    val engine: String = ""// Motor hacmi / tipi (ör. 1.5 dCi) — isteğe bağlı
)

// Karşılama ekranı (onboarding) için her sayfanın içeriği
data class OnboardingPage(val title: String, val description: String, val iconName: String)

// Uygulama genelinde hizmet kategorisi (ör. Araba Yıkama, Oto Bakım)
data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val averagePrice: Int,  // Ortalama ücret (₺)
    val duration: String,   // Tahmini süre (ör. "45-60 dk")
    val iconName: String    // İkon adı → Components.getServiceIcon() ile çözülür
)

// Bir işletmenin sunduğu tekil hizmet ve fiyatı
data class BusinessService(
    val id: Int    = 0,
    val name: String = "",
    val price: Int = 0
)

// Bir servis işletmesinin tüm bilgileri
data class Business(
    val id: Int,
    val name: String,
    val rating: Float,          // Ortalama kullanıcı puanı (1.0–5.0)
    val distanceKm: Float,      // Mesafe km cinsinden
    val distanceText: String,   // Gösterim metni (ör. "1.2 km")
    val startingPrice: Int,     // Başlangıç fiyatı — kart alt kısmında gösterilir
    val isVerified: Boolean,    // OtoGüven tarafından onaylanmış mı?
    val address: String,
    val hasValet: Boolean,      // Vale hizmeti sunuyor mu? İşletme panelinden açılır.
    val onSiteService: Boolean, // Yerinde hizmet sunuyor mu? İşletme panelinden açılır.
    val isAvailable: Boolean,   // Şu an randevu kabul ediyor mu?
    val type: BusinessType,
    val phone: String = "",
    val services: MutableList<BusinessService> = mutableListOf() // İşletmenin hizmet listesi
)

// Bir müşteri randevusunun tüm bilgileri
data class Appointment(
    val id: Int,
    val businessName: String,
    val serviceName: String,
    val date: String,            // Seçilen tarih (ör. "12 Mayıs 2026")
    val time: String,            // Seçilen saat (ör. "10:30")
    val totalPrice: Int,         // Toplam ücret (₺)
    val vehicleName: String,     // Araç açıklaması (ör. "Renault Clio 4 2019 (Dizel)")
    val status: AppointmentStatus,
    val hasValet: Boolean = false,      // Vale talebi var mı?
    val valetAddress: String = "",      // Vale için araç teslim adresi
    val isOnSite: Boolean = false,      // Yerinde hizmet talebi var mı?
    val onSiteAddress: String = "",     // Yerinde hizmet için hizmet adresi
    val userRating: Int = 0             // Kullanıcının verdiği puan (0 = henüz verilmedi, 1–5)
)
