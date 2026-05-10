package com.yusa.autolink.data

data class ServicePackage(val name: String, val multiplier: Float)

val washingPackages = listOf(
    ServicePackage("Standart Yıkama",        1.0f),
    ServicePackage("Köpük & Balmumu",        1.4f),
    ServicePackage("Detaylı İç-Dış Yıkama",  1.8f),
    ServicePackage("Profesyonel Cila",        2.8f)
)

val maintenancePackages = listOf(
    ServicePackage("Yağ Değişimi",           1.0f),
    ServicePackage("Yağ + Filtre Seti",      1.3f),
    ServicePackage("Fren Sistemi Kontrolü",  1.6f),
    ServicePackage("Komple Bakım Paketi",    2.2f)
)
