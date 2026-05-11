package com.yusa.autolink

import android.app.Application
import com.yusa.autolink.data.AppState

// ============================================================
// AutoLinkApp — Özel Application sınıfı
//
// Android her uygulamayı başlatırken önce Application nesnesini
// oluşturur, sonra MainActivity'yi açar. Bu sınıf sayesinde
// herhangi bir Activity açılmadan önce AppState.load() çağrılır
// ve SharedPreferences'taki kayıtlı veriler belleğe yüklenir.
//
// AndroidManifest.xml'de "android:name=".AutoLinkApp"" ile
// kayıtlı olduğu için Android bu sınıfı otomatik kullanır.
// Kaydedilmeseydi varsayılan Application sınıfı çalışırdı
// ve load() hiç çağrılmazdı — veriler yüklenemezdi.
// ============================================================
class AutoLinkApp : Application() {

    // onCreate → Uygulama süreci ilk başladığında bir kez çağrılır.
    // MainActivity.onCreate()'den önce çalışır, bu yüzden veri yükleme buraya konuldu.
    override fun onCreate() {
        super.onCreate()
        // Kayıtlı kullanıcıları, işletmeleri ve aktif oturumu cihazdan yükle.
        // Context (this) gerekir çünkü SharedPreferences'a erişim için Android sistem
        // izni lazımdır — AppState bunu kendi başına alamaz, Activity/Application verir.
        AppState.load(this)
    }
}
