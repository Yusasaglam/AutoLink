# Proje Klasör Yapısı — OtoGüven

```
AutoLink/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/yusa/autolink/
│           │   │
│           │   ├── MainActivity.kt              # Tek Activity — AppNavigation'ı çağırır
│           │   │
│           │   ├── data/
│           │   │   ├── model/
│           │   │   │   └── Models.kt            # Tüm data class ve enum'lar
│           │   │   ├── DemoData.kt              # Mock veriler (işletme, randevu, araç)
│           │   │   └── AppState.kt              # Geçici global durum (giriş, sekme, randevu)
│           │   │
│           │   ├── navigation/
│           │   │   └── AppNavigation.kt         # Tüm rota tanımları ve geçiş mantığı
│           │   │
│           │   └── ui/
│           │       ├── theme/
│           │       │   ├── Color.kt             # OtoGüven renk paleti
│           │       │   ├── Theme.kt             # Material3 tema kurulumu
│           │       │   └── Type.kt              # Typography
│           │       │
│           │       ├── components/
│           │       │   └── Components.kt        # Paylaşılan UI bileşenleri
│           │       │
│           │       └── screens/
│           │           ├── SplashScreen.kt          # Açılış ekranı
│           │           ├── LoginScreen.kt           # Giriş ekranı
│           │           ├── RegisterScreen.kt        # Kayıt ekranı (Müşteri/İşletme)
│           │           ├── MainScreen.kt            # Alt menü sarmalayıcı ← YENİ
│           │           ├── HomeScreen.kt            # Ana sayfa (2 hizmet seçeneği)
│           │           ├── BusinessListScreen.kt    # İşletme listesi + filtreler
│           │           ├── AppointmentScreen.kt     # Randevu oluşturma
│           │           ├── AppointmentSuccessScreen.kt  # Randevu başarı
│           │           ├── MyAppointmentsScreen.kt  # Randevularım sekmesi ← YENİ
│           │           ├── MyVehiclesScreen.kt      # Araçlarım sekmesi ← YENİ
│           │           ├── ProfileScreen.kt         # Profil ve çıkış
│           │           ├── ServiceDetailScreen.kt   # (aktif navda yok, derleniyor)
│           │           └── OnboardingScreen.kt      # (aktif navda yok, derleniyor)
│           │
│           └── res/
│               ├── values/
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── mipmap-*/                    # Uygulama ikonu
│
├── PROJECT_CONTEXT.md    # Proje genel bağlamı ve mimari kararlar
├── CHANGELOG.md          # Versiyon geçmişi
├── TODO.md               # Yapıldı / yapılmayacak listesi
├── PROJECT_STRUCTURE.md  # Bu dosya
├── UI_COMPONENTS.md      # Paylaşılan component açıklamaları
└── MOCK_DATA.md          # Demo veri açıklamaları
```

## Klasörlerin Amacı

| Klasör | Açıklama |
|---|---|
| `data/model/` | Kotlin data class'ları — verinin şeklini tanımlar |
| `data/` | Demo veriler ve geçici uygulama durumu |
| `navigation/` | Ekranlar arası geçiş mantığı (rotalar + NavHost) |
| `ui/theme/` | Renkler, tipografi, Compose tema |
| `ui/components/` | Birden fazla ekranda kullanılan UI parçaları |
| `ui/screens/` | Her ekran kendi `.kt` dosyasında |

## Navigasyon Özeti

| Rota | Ekran |
|---|---|
| `splash` | SplashScreen |
| `login` | LoginScreen |
| `register` | RegisterScreen |
| `main` | MainScreen (4 sekme içerir) |
| `business_list/{serviceType}` | BusinessListScreen |
| `appointment/{businessId}/{serviceType}` | AppointmentScreen |
| `appointment_success` | AppointmentSuccessScreen |
