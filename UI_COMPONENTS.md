# UI Bileşenleri — OtoGüven

Paylaşılan bileşenler `ui/components/Components.kt` dosyasındadır.  
Sadece bu dosyada tanımlanan, birden fazla ekranda kullanılan parçalar buradadır.

---

## VehicleCard
**Ne işe yarıyor:** Kullanıcının kayıtlı aracını mavi arka planlı bir kartla gösterir.  
**Nerede kullanılıyor:** `HomeScreen`  
**Parametreler:**
| Parametre | Tür | Açıklama |
|---|---|---|
| `vehicle` | `Vehicle` | Gösterilecek araç verisi |
| `modifier` | `Modifier` | İsteğe bağlı boyut/padding |

---

## BusinessCard
**Ne işe yarıyor:** Bir işletmeyi; adı, puanı, mesafesi, fiyatı, rozetleri ve "Randevu Al" butonu ile gösterir.  
**Nerede kullanılıyor:** `BusinessListScreen`  
**Rozetler:** Onaylı İşletme · Vale · Müsait/Meşgul · Yerinde Hizmet  
**Parametreler:**
| Parametre | Tür | Açıklama |
|---|---|---|
| `business` | `Business` | Gösterilecek işletme verisi |
| `onAppointmentClick` | `() -> Unit` | Randevu al tıklaması |
| `modifier` | `Modifier` | İsteğe bağlı |

---

## TrustBadge
**Ne işe yarıyor:** "Onaylı İşletme", "Vale", "Müsait" gibi küçük renkli rozetler gösterir.  
**Nerede kullanılıyor:** `BusinessCard` içinde  
**Parametreler:**
| Parametre | Tür | Açıklama |
|---|---|---|
| `text` | `String` | Rozet metni |
| `backgroundColor` | `Color` | Arka plan rengi |
| `textColor` | `Color` | Yazı rengi |

---

## PrimaryButton
**Ne işe yarıyor:** Birincil eylem butonu. Tam genişlik, 56dp yükseklik.  
**Nerede kullanılıyor:** `AppointmentScreen`, `AppointmentSuccessScreen`  
**Parametreler:**
| Parametre | Tür | Açıklama |
|---|---|---|
| `text` | `String` | Buton metni |
| `onClick` | `() -> Unit` | Tıklama işlemi |
| `modifier` | `Modifier` | İsteğe bağlı |
| `enabled` | `Boolean` | Aktif mi? (default: true) |

---

## StatusBadge (MyAppointmentsScreen içinde)
**Ne işe yarıyor:** Randevunun durumunu renkli rozet olarak gösterir.  
**Renk kodlaması:**
| Durum | Renk | Metin |
|---|---|---|
| `CONFIRMED` | Yeşil | Onaylandı |
| `PENDING` | Turuncu | Beklemede |
| `COMPLETED` | Mavi | Tamamlandı |

---

## getServiceIcon (Yardımcı Fonksiyon)
**Ne işe yarıyor:** Hizmetin `iconName` değerini Material Icon'a dönüştürür.  
**Nerede kullanılıyor:** `ServiceCard`
