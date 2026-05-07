package com.yusa.autolink.data.model

// İşletme türü - yıkama mı bakım mı?
enum class BusinessType { WASHING, MAINTENANCE }

// Kullanıcı hesap türü - kayıt ekranında seçilir
enum class AccountType { CUSTOMER, BUSINESS }

// Randevu durumu - listede her kartın sağ üstünde rozet olarak gösterilir
enum class AppointmentStatus { CONFIRMED, PENDING, COMPLETED }

// Araç modeli
data class Vehicle(
    val id:       Int,
    val brand:    String,
    val model:    String,
    val year:     Int,
    val plate:    String,
    val fuelType: String,
    val engine:   String = ""   // Motor bilgisi (örn: "1.5 dCi")
)

// Hizmet modeli (ServiceDetailScreen için geriye dönük uyumluluk)
data class Service(
    val id:           Int,
    val name:         String,
    val description:  String,
    val averagePrice: Int,
    val duration:     String,
    val iconName:     String
)

// İşletme modeli
data class Business(
    val id:            Int,
    val name:          String,
    val rating:        Float,
    val distanceKm:    Float,       // Mesafe (km) - filtreleme/sıralama için
    val distanceText:  String,      // Mesafe gösterimi ("1.2 km")
    val startingPrice: Int,         // Başlangıç fiyatı (TL)
    val isVerified:    Boolean,     // OtoGüven onaylı işletme
    val address:       String,
    val hasValet:      Boolean,     // Vale hizmeti var mı?
    val onSiteService: Boolean,     // Yerinde hizmet veriyor mu?
    val isAvailable:   Boolean,     // Şu an müsait mi?
    val type:          BusinessType // Hizmet türü
)

// Randevu modeli
data class Appointment(
    val id:           Int,
    val businessName: String,
    val serviceName:  String,
    val date:         String,
    val time:         String,
    val totalPrice:   Int,
    val vehicleName:  String,
    val status:       AppointmentStatus = AppointmentStatus.CONFIRMED
)

// Demo kullanıcı modeli
data class User(
    val name:  String,
    val phone: String,
    val email: String
)

// Onboarding sayfası modeli
data class OnboardingPage(
    val title:       String,
    val description: String,
    val iconName:    String
)
