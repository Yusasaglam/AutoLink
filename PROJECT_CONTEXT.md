# OtoGüven — Proje Bağlamı

## Projenin Amacı
OtoGüven, araç sahiplerinin güvenilir oto yıkama ve oto bakım işletmelerini görüp randevu oluşturabildiği demo bir Android mobil uygulamasıdır. Okul sunumu için hazırlanmıştır; gerçek backend, ödeme veya harita entegrasyonu yoktur.

## Kullanılan Teknolojiler
| Teknoloji | Kullanım Amacı |
|---|---|
| Kotlin | Uygulama dili |
| Jetpack Compose | UI framework |
| Material 3 | Tasarım sistemi (NavigationBar, Card, FilterChip vb.) |
| Navigation Compose | Ekranlar arası geçiş |
| Compose Animation | Splash fade-in |

## Mimari Karar
- **No-architecture** yaklaşımı: MVVM, Repository, Room, Retrofit kullanılmamıştır.
- Tüm veriler `DemoData.kt` içindeki statik listelerden gelir.
- Ekranlar arası geçici veri aktarımı için `AppState.kt` kullanılır.
- Kod kasıtlı olarak sade tutulmuştur; öğrenci sunumda her satırı açıklayabilmelidir.

## Uygulama Akışı

```
Splash Screen (2sn)
    → Login Screen
        → Register Screen (geri → Login)
        → Ana Ekran (MainScreen)
            ├── [Alt Menü — Sekme 0] Home Screen
            │       → Business List Screen (washing/maintenance)
            │               → Appointment Screen
            │                       → Appointment Success Screen
            │                               → Randevularım (sekme 1) veya Ana Sayfa (sekme 0)
            ├── [Alt Menü — Sekme 1] Randevularım
            ├── [Alt Menü — Sekme 2] Araçlarım
            └── [Alt Menü — Sekme 3] Profil → Çıkış Yap → Login
```

## Ekranlar ve Görevleri
| Ekran | Dosya | Görev |
|---|---|---|
| Splash | `SplashScreen.kt` | Fade-in logo, 2sn bekle |
| Giriş | `LoginScreen.kt` | E-posta/şifre, demo giriş |
| Kayıt | `RegisterScreen.kt` | Müşteri/İşletme seçimi |
| Ana Ekran Sarmalayıcı | `MainScreen.kt` | Alt menü (4 sekme) |
| Ana Sayfa | `HomeScreen.kt` | Araç kartı + 2 hizmet seçeneği |
| İşletme Listesi | `BusinessListScreen.kt` | Filtreleme + işletme kartları |
| Randevu | `AppointmentScreen.kt` | Tarih/saat seç, onayla |
| Randevu Başarı | `AppointmentSuccessScreen.kt` | Özet + yönlendirme butonları |
| Randevularım | `MyAppointmentsScreen.kt` | Randevu listesi + durum rozetleri |
| Araçlarım | `MyVehiclesScreen.kt` | Araç listesi + ekleme/düzenleme formu |
| Profil | `ProfileScreen.kt` | Kullanıcı bilgisi, destek, çıkış |

## Tema ve Tasarım Sistemi
- **Birincil renk:** `#1565C0` (mavi — güvenilirlik)
- **Arka plan:** `#F5F7FA` (açık gri)
- **Yüzey:** `#FFFFFF` (beyaz kart)
- **Başarı yeşili:** `#2E7D32` (fiyat, onay)
- **Puan sarısı:** `#FFC107`
- **Hata kırmızısı:** `#D32F2F` (çıkış butonu)
- Corner radius: 12–20dp · Elevation: 2–4dp

## Kullanıcı Rolleri
- **Müşteri (demo):** Ahmet Yılmaz — randevu alır, araç yönetir
- İşletme sahipleri veya admin paneli bu demo'da yoktur.

## Gelecek Geliştirmeler (Kapsam Dışı)
- Gerçek kullanıcı girişi (Firebase Auth veya API)
- Harita entegrasyonu (Google Maps)
- Anlık bildirimler
- Ödeme entegrasyonu
- İşletme yönetim paneli
