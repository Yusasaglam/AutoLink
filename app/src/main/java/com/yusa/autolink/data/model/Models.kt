package com.yusa.autolink.data.model

// ============================================================
// Models.kt — Uygulamanın tüm veri modelleri
//
// Bu dosya OOP'un temel yapı taşlarını içerir:
//   • enum class  → sabit değer kümeleri (tip güvenliği sağlar)
//   • data class  → veri tutan nesneler (equals/copy otomatik gelir)
//
// Tüm modeller buraya toplandı; hangi verinin ne anlama
// geldiği tek yerden takip edilebilir.
// ============================================================

// Kullanıcının hesap tipi — müşteri mi işletme sahibi mi?
// String "musteri"/"isletme" yerine enum kullanıldı: yanlış yazım imkânsız.
enum class AccountType { CUSTOMER, BUSINESS }

// İşletmenin hizmet türü — araba yıkama veya oto bakım
// BusinessListScreen bu değere göre filtreleme yapar.
enum class BusinessType { WASHING, MAINTENANCE }

// Bir randevunun yaşam döngüsü:
// PENDING (beklemede) → CONFIRMED (onaylandı) → COMPLETED (tamamlandı)
//                     ↘ CANCELLED (iptal)
// İşletme PENDING olanı onaylar ya da reddeder.
// Müşteri PENDING olanı iptal edebilir.
// İşletme CONFIRMED olanı tamamlandı olarak işaretler.
enum class AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }

// Temel kullanıcı bilgisi — AppState'teki RegisteredUser daha kapsamlıdır,
// bu model eski uyumluluk için tutulmaktadır.
data class User(val name: String, val phone: String, val email: String)

// Kullanıcının kayıtlı aracı
// engine alanı isteğe bağlıdır (default = "") çünkü bazı kullanıcılar
// motor bilgisini bilmeyebilir veya girmek istemeyebilir.
data class Vehicle(
    val id: Int,           // Her aracın benzersiz numarası — güncelleme/silmede kullanılır
    val brand: String,     // Marka  (ör. Renault, BMW)
    val model: String,     // Model  (ör. Clio 4, 3 Serisi)
    val year: Int,         // Model yılı (ör. 2019)
    val plate: String,     // Plaka  (ör. 34 ABC 123)
    val fuelType: String,  // Yakıt tipi: Benzin, Dizel, LPG, Elektrik, Hibrit
    val engine: String = ""// Motor bilgisi (ör. 1.5 dCi) — boş bırakılabilir
)

// Bir işletmenin sunduğu tekil hizmet ve fiyatı
// AppointmentScreen bu listeyi gösterir; kullanıcı birini seçer.
// id alanı hizmet silinirken hangi kaydın kaldırılacağını belirler.
data class BusinessService(
    val id: Int    = 0,
    val name: String = "",
    val price: Int = 0   // Türk Lirası cinsinden sabit fiyat
)

// Bir servis işletmesinin tüm bilgileri
// DemoData'daki sabit işletmeler ve kullanıcıların oluşturduğu
// işletmeler aynı Business modeline uyar.
data class Business(
    val id: Int,
    val name: String,
    val rating: Float,          // Ortalama kullanıcı puanı (1.0 – 5.0)
    val distanceKm: Float,      // Mesafe — km cinsinden, sıralama için kullanılır
    val distanceText: String,   // Görüntüleme metni  (ör. "1.2 km", "800 m")
    val startingPrice: Int,     // Başlangıç fiyatı — işletme kartının altında gösterilir
    val isVerified: Boolean,    // OtoGüven tarafından onaylanmış mı? (rozet gösterilir)
    val address: String,
    val hasValet: Boolean,      // Vale hizmeti var mı? — işletme sahibi açıp kapatabilir
    val onSiteService: Boolean, // Yerinde (müşteri adresine) hizmet var mı?
    val isAvailable: Boolean,   // Şu an randevu kabul ediyor mu? ("Müsait"/"Meşgul" rozeti)
    val type: BusinessType,     // WASHING veya MAINTENANCE — listeyi filtrelemek için
    val phone: String = "",
    val services: MutableList<BusinessService> = mutableListOf() // İşletmenin hizmet/fiyat listesi
)

// Bir müşteri randevusunun tüm bilgileri
// userRating = 0 → kullanıcı henüz puan vermemiş demektir.
// hasValet / isOnSite → randevu sırasında seçilen teslimat tipi.
data class Appointment(
    val id: Int,
    val businessName: String,
    val serviceName: String,
    val date: String,            // Seçilen tarih metni (ör. "12 Mayıs 2026")
    val time: String,            // Seçilen saat       (ör. "10:30")
    val totalPrice: Int,         // Toplam ücret (₺)
    val vehicleName: String,     // Araç açıklaması    (ör. "Renault Clio 4 2019 (Dizel)")
    val status: AppointmentStatus,
    val hasValet: Boolean = false,      // Vale talebi var mı?
    val valetAddress: String = "",      // Vale için araç teslim adresi
    val isOnSite: Boolean = false,      // Yerinde hizmet talebi var mı?
    val onSiteAddress: String = "",     // Yerinde hizmet için hizmet adresi
    val userRating: Int = 0             // 0 = puan verilmedi, 1–5 = yıldız sayısı
)
