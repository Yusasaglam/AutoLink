# CHANGELOG — OtoGüven

## 2026-05-07 — v1.2.0 · Bottom Nav + Randevularım + Araçlarım

### Yeni Ekranlar
- `MainScreen.kt` — Alt menü sarmalayıcı (NavigationBar: Ana Sayfa / Randevularım / Araçlarım / Profil)
- `MyAppointmentsScreen.kt` — Randevu listesi; her kartta işletme, hizmet, araç, tarih, durum rozeti, ücret
- `MyVehiclesScreen.kt` — Kayıtlı araç listesi + ekleme/düzenleme formu (Marka, Model, Yıl, Motor, Yakıt Tipi)

### Değiştirilen Ekranlar
- `AppointmentSuccessScreen.kt` — "Randevularımı Gör" butonu eklendi → direkt Randevularım sekmesine gider
- `ProfileScreen.kt` — Basitleştirildi: randevu/araç bölümleri kendi sekmelerine taşındı; "Çıkış Yap" kırmızı butonu eklendi
- `HomeScreen.kt` — `onNavigateToProfile` parametresi kaldırıldı (profil artık alt menüde)

### Değiştirilen Modeller
- `AppointmentStatus` enum eklendi: `CONFIRMED`, `PENDING`, `COMPLETED`
- `Appointment` — `status: AppointmentStatus` alanı eklendi
- `Vehicle` — `engine: String = ""` alanı eklendi (Motor bilgisi)

### Değiştirilen AppState
- `isLoggedIn: Boolean` eklendi — demo giriş durumu
- `selectedTab: Int` eklendi — başarı ekranından hangi sekmenin açılacağını belirler

### Navigasyon Değişiklikleri
- `HOME` rotası → `MAIN` rotası (MainScreen barındırır)
- `PROFILE` rotası kaldırıldı (artık MainScreen'deki sekme)
- Çıkış akışı: ProfileScreen → AppState.isLoggedIn=false → LOGIN

### Demo Data
- Randevulara `status` alanı eklendi: 1=Tamamlandı, 2=Onaylandı, 3=Beklemede
- 3. demo randevu eklendi: "Mobil Yıkama Pro — Beklemede"
- `userVehicle` güncellendi: model="Clio 4", engine="1.5 dCi" (ayrı alanlar)

---

## 2026-05-07 — v1.1.0 · Giriş/Kayıt + Filtreleme

### Eklenen Ekranlar
- `LoginScreen.kt` — E-posta/şifre girişi, "Kayıt Ol" linki
- `RegisterScreen.kt` — Müşteri/İşletme seçimi, kayıt formu

### Değiştirilen Ekranlar
- `HomeScreen.kt` — 4 hizmet kartı yerine 2 büyük seçenek kartı (Araba Yıkama, Oto Bakım)
- `BusinessListScreen.kt` — Filtreleme sistemi eklendi (vale, yerinde, puan, mesafe, fiyat, müsaitlik)
- `AppointmentScreen.kt` — `serviceId: Int` → `serviceType: String` ("washing"/"maintenance")

### Değiştirilen Componentler
- `BusinessCard` — Yeni alanlar: vale rozeti, müsaitlik rozeti, yerinde hizmet rozeti

### Değiştirilen Modeller
- `Business` — Yeni alanlar: `distanceKm`, `distanceText`, `hasValet`, `onSiteService`, `isAvailable`, `type`
- `BusinessType` enum eklendi: `WASHING`, `MAINTENANCE`
- `AccountType` enum eklendi: `CUSTOMER`, `BUSINESS`

### Navigasyon Değişiklikleri
- Yeni rotalar: `LOGIN`, `REGISTER`
- Splash → Login (önceden Onboarding'e gidiyordu)
- `BUSINESS_LIST/{serviceType}` (önceden `{serviceId: Int}`)
- `APPOINTMENT/{businessId}/{serviceType}`

### Demo Data
- 4 yıkama + 3 bakım işletmesi (toplamda 7 işletme)
- Her işletmede `hasValet`, `onSiteService`, `isAvailable`, `type` alanları

---

## 2026-05-07 — v1.0.0 · İlk Demo Sürümü

### Eklenen Ekranlar
- SplashScreen, OnboardingScreen, HomeScreen, ServiceDetailScreen,
  BusinessListScreen, AppointmentScreen, AppointmentSuccessScreen, ProfileScreen

### Eklenen Componentler
- VehicleCard, ServiceCard, BusinessCard, TrustBadge, PrimaryButton

### Eklenen Dosyalar
- DemoData.kt, AppState.kt, Models.kt, AppNavigation.kt
- Color.kt, Theme.kt, strings.xml, themes.xml güncellendi
