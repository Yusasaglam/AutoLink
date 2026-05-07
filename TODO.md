# TODO — OtoGüven

## Yapıldı ✅

### Temel Akış
- [x] Splash Screen (fade-in animasyon, 2sn)
- [x] Login Screen (e-posta + şifre, demo giriş)
- [x] Register Screen (Müşteri / İşletme seçimi)
- [x] Çıkış Yap (ProfileScreen → Login)

### Ana Ekran & Navigasyon
- [x] Home Screen (sadece 2 seçenek: Araba Yıkama, Oto Bakım)
- [x] Bottom Navigation (Ana Sayfa / Randevularım / Araçlarım / Profil)
- [x] MainScreen sarmalayıcı — alt menü yönetimi

### İşletme & Randevu
- [x] Business List Screen + Filtreleme sistemi
  - [x] Vale hizmeti filtresi
  - [x] Yerinde hizmet filtresi
  - [x] Yüksek puan sıralaması
  - [x] En yakın sıralaması
  - [x] En uygun fiyat sıralaması
  - [x] Müsait olanlar filtresi
- [x] İşletme kartı: ad, puan, fiyat, mesafe, vale, müsaitlik, onaylı rozeti
- [x] Appointment Screen (tarih/saat seçimi, net fiyat özeti)
- [x] Appointment Success Screen ("Randevularımı Gör" + "Ana Sayfaya Dön")

### Randevularım & Araçlarım
- [x] Randevularım sekmesi — durum rozetleri (Onaylandı / Beklemede / Tamamlandı)
- [x] Araçlarım sekmesi — kayıtlı araç listesi
- [x] Araç ekleme formu (Marka, Model, Yıl, Motor, Yakıt Tipi)
- [x] Araç düzenleme formu

### Altyapı
- [x] Demo Data (7 işletme: 4 yıkama + 3 bakım)
- [x] AppointmentStatus enum (Onaylandı / Beklemede / Tamamlandı)
- [x] AppState (isLoggedIn, selectedTab, son randevu bilgileri)
- [x] Veri modelleri (Vehicle engine alanı, Appointment status alanı)

---

## Yapılmayacak (Demo Kapsam Dışı)
- Firebase Auth / gerçek giriş
- Harita entegrasyonu (Google Maps)
- Gerçek işletme veritabanı / API
- Anlık bildirimler
- Ödeme entegrasyonu
- Dark mode
- İşletme yönetim paneli
