package com.yusa.autolink.data

// Hizmet paketi: isim ve fiyat çarpanı
// Çarpan 1.0 = temel fiyat, 2.0 = iki katı fiyat
data class ServicePackage(val name: String, val multiplier: Float)

// Araba yıkama paketleri — fiyat temel ücret × çarpan ile hesaplanır
val washingPackages = listOf(
    ServicePackage("Standart Yıkama",        1.0f),
    ServicePackage("Köpük & Balmumu",        1.4f),
    ServicePackage("Detaylı İç-Dış Yıkama",  1.8f),
    ServicePackage("Profesyonel Cila",        2.8f)
)

// Oto bakım paketleri — fiyat temel ücret × çarpan ile hesaplanır
val maintenancePackages = listOf(
    ServicePackage("Yağ Değişimi",           1.0f),
    ServicePackage("Yağ + Filtre Seti",      1.3f),
    ServicePackage("Fren Sistemi Kontrolü",  1.6f),
    ServicePackage("Komple Bakım Paketi",    2.2f)
)
