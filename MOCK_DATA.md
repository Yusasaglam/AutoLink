# Mock / Demo Veriler — OtoGüven

Tüm veriler `app/.../data/DemoData.kt` içindedir.

---

## Demo Kullanıcı
```
Ahmet Yılmaz | 0532 123 45 67 | ahmet.yilmaz@gmail.com
```

## Demo Araç
```
Renault Clio 4 1.5 dCi — 2012 — 34 ABC 123 — Dizel
```

---

## Araba Yıkama İşletmeleri (4 adet)

| # | Ad | Puan | Mesafe | Fiyat | Onaylı | Vale | Yerinde | Müsait |
|---|---|---|---|---|---|---|---|---|
| 1 | Star Oto Kuaför       | 4.8 | 1.2 km | ₺280 | Evet | Evet | Hayır | Evet |
| 2 | Hızlı Yıkama Merkezi  | 4.3 | 800 m  | ₺200 | Hayır| Hayır| Evet | Evet |
| 3 | Premium Oto Spa       | 4.9 | 2.5 km | ₺450 | Evet | Evet | Hayır | Hayır|
| 4 | Mobil Yıkama Pro      | 4.5 | 1.8 km | ₺320 | Evet | Hayır| Evet | Evet |

## Oto Bakım İşletmeleri (3 adet)

| # | Ad | Puan | Mesafe | Fiyat | Onaylı | Vale | Müsait |
|---|---|---|---|---|---|---|---|
| 5 | Güven Oto Bakım Merkezi | 4.6 | 2.5 km | ₺1650 | Evet | Hayır | Evet |
| 6 | Pro Teknik Servis        | 4.9 | 3.1 km | ₺1200 | Evet | Hayır | Hayır|
| 7 | Hızlı Lastik & Rot Balans| 4.4 | 1.5 km | ₺500  | Hayır| Hayır | Evet |

---

## Filtreleme Davranışı
- **Vale / Yerinde / Müsait**: checkbox mantığı — seçilince sadece o özelliğe sahip işletmeler gösterilir
- **Yüksek Puan / En Yakın / En Uygun Fiyat**: sıralama mantığı — birbirleriyle çakışmaz, biri seçilince diğerleri sıfırlanır

---

## AppState (Randevu Geçici Verisi)
| Alan | Açıklama |
|---|---|
| `lastBusinessName` | Seçilen işletme |
| `lastServiceName`  | "Araba Yıkama" veya "Oto Bakım" |
| `lastDate`         | Seçilen tarih |
| `lastTime`         | Seçilen saat |
| `lastPrice`        | Net hizmet bedeli (TL) |

---

## Kayıt Türleri (AccountType)
- **CUSTOMER** → Araç sahibi — varsayılan akış (randevu al)
- **BUSINESS** → İşletme sahibi — ileride işletme yönetim paneli için

---

## Geçmiş Randevular (Profil ekranında gösterilir)
1. Star Oto Kuaför — Araba Yıkama — 12 Mayıs 2026 10:30 — ₺320
2. Güven Oto Bakım Merkezi — Oto Bakım — 20 Mayıs 2026 14:00 — ₺1850
