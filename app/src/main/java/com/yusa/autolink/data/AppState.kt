package com.yusa.autolink.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.runtime.mutableStateListOf
import com.yusa.autolink.data.model.AccountType
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.Business
import com.yusa.autolink.data.model.BusinessService
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.data.model.Vehicle

// AppState — uygulamanın merkezi veri yöneticisi.
// Tüm kullanıcılar, araçlar, randevular ve işletmeler burada tutulur.
// "object" anahtar kelimesi: uygulama boyunca tek bir örnek vardır (Singleton).
object AppState {

    // ── Aktif oturum bilgileri ───────────────────────────────────────────────
    var isLoggedIn: Boolean = false
    var selectedTab: Int = 0           // Alt menüde hangi sekme açık?

    var currentUserName: String = ""
    var currentUserEmail: String = ""
    var currentUserPhone: String = ""
    var currentAccountType: AccountType = AccountType.CUSTOMER
    var currentBusinessId: Int = -1    // -1 = müşteri hesabı (işletme yok)

    // Son alınan randevunun bilgileri — başarı ekranında göstermek için saklanır
    var lastBusinessName: String = ""
    var lastServiceName: String = ""
    var lastDate: String = ""
    var lastTime: String = ""
    var lastPrice: Int = 0

    // ── Kayıtlı kullanıcı modeli ─────────────────────────────────────────────
    // Her kullanıcının kendi araç ve randevu listeleri vardır.
    data class RegisteredUser(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val accountType: AccountType = AccountType.CUSTOMER,
        val businessId: Int = -1,
        val vehicles: MutableList<Vehicle> = mutableListOf(),
        val appointments: MutableList<Appointment> = mutableListOf()
    )

    // Tüm kayıtlı kullanıcıların listesi
    val registeredUsers = mutableListOf<RegisteredUser>()

    // Kullanıcıların oluşturduğu işletmeler (id >= 100)
    val userCreatedBusinesses = mutableListOf<Business>()

    // Aktif kullanıcının araç ve randevu listelerine kısayol referanslar.
    // Login/register sırasında ilgili kullanıcının listesine yönlendirilir.
    var userVehicles: MutableList<Vehicle> = mutableListOf()
    var userAppointments: MutableList<Appointment> = mutableListOf()

    // Tüm kullanıcıların randevularının birleşik listesi — işletme paneli bu listeyi okur
    // SnapshotStateList: Compose değişiklikleri otomatik algılar ve ekranları günceller
    val allAppointments = mutableStateListOf<Appointment>()

    // allAppointments listesini registeredUsers'daki tüm randevulardan yeniden oluşturur.
    // Durum değişikliklerinden sonra çağrılır.
    fun rebuildAllAppointments() {
        allAppointments.clear()
        registeredUsers.forEach { allAppointments.addAll(it.appointments) }
    }

    // Randevu durumunu günceller (onayla, reddet, tamamla).
    // Tüm kullanıcılar taranır çünkü hangi kullanıcıya ait olduğu bilinmez.
    fun updateAppointmentStatus(appointmentId: Int, newStatus: AppointmentStatus) {
        registeredUsers.forEach { user ->
            val idx = user.appointments.indexOfFirst { it.id == appointmentId }
            if (idx >= 0) user.appointments[idx] = user.appointments[idx].copy(status = newStatus)
        }
        rebuildAllAppointments()
        save()
    }

    // Aktif kullanıcının bir randevusuna 1–5 arası puan verir.
    // Eğer işletme kullanıcı tarafından oluşturulmuşsa işletmenin ortalama puanı güncellenir.
    fun rateAppointment(appointmentId: Int, rating: Int) {
        val idx = userAppointments.indexOfFirst { it.id == appointmentId }
        if (idx >= 0) userAppointments[idx] = userAppointments[idx].copy(userRating = rating)
        val businessName = userAppointments.getOrNull(idx)?.businessName ?: ""
        val bizIdx = userCreatedBusinesses.indexOfFirst { it.name == businessName }
        if (bizIdx >= 0) {
            // Tüm kullanıcıların bu işletmeye verdiği puanların ortalamasını hesapla
            val ratings = registeredUsers
                .flatMap { it.appointments }
                .filter { it.businessName == businessName && it.userRating > 0 }
                .map { it.userRating }
            if (ratings.isNotEmpty()) {
                val avg = ratings.average().toFloat()
                userCreatedBusinesses[bizIdx] = userCreatedBusinesses[bizIdx].copy(rating = avg)
            }
        }
        rebuildAllAppointments()
        save()
    }

    // İşletme sahibinin vale hizmetini açıp kapatması
    fun setBusinessValet(enabled: Boolean) {
        val idx = userCreatedBusinesses.indexOfFirst { it.id == currentBusinessId }
        if (idx >= 0) userCreatedBusinesses[idx] = userCreatedBusinesses[idx].copy(hasValet = enabled)
        save()
    }

    // İşletme sahibinin yerinde hizmet seçeneğini açıp kapatması
    fun setBusinessOnSite(enabled: Boolean) {
        val idx = userCreatedBusinesses.indexOfFirst { it.id == currentBusinessId }
        if (idx >= 0) userCreatedBusinesses[idx] = userCreatedBusinesses[idx].copy(onSiteService = enabled)
        save()
    }

    // İşletmeye yeni hizmet ekler ve başlangıç fiyatını günceller
    fun addBusinessService(name: String, price: Int) {
        val idx = userCreatedBusinesses.indexOfFirst { it.id == currentBusinessId }
        if (idx < 0) return
        val biz = userCreatedBusinesses[idx]
        val services = (biz.services ?: mutableListOf()).toMutableList()
        val nextId = (services.maxOfOrNull { it.id } ?: 0) + 1
        services.add(BusinessService(id = nextId, name = name, price = price))
        // Başlangıç fiyatı her zaman en ucuz hizmete eşittir
        val newStartingPrice = services.minOf { it.price }
        userCreatedBusinesses[idx] = biz.copy(startingPrice = newStartingPrice, services = services)
        save()
    }

    // İşletmeden mevcut bir hizmeti siler
    fun removeBusinessService(serviceId: Int) {
        val idx = userCreatedBusinesses.indexOfFirst { it.id == currentBusinessId }
        if (idx < 0) return
        val biz = userCreatedBusinesses[idx]
        val services = (biz.services ?: mutableListOf()).toMutableList()
        services.removeIf { it.id == serviceId }
        val newStartingPrice = services.minOfOrNull { it.price } ?: biz.startingPrice
        userCreatedBusinesses[idx] = biz.copy(startingPrice = newStartingPrice, services = services)
        save()
    }

    // ── ID sayaçları (her yeni kayıt için 1 artar) ───────────────────────────
    private var nextBusinessId = 100      // Demo işletmeler 1–99 arası, kullanıcı işletmeleri 100+
    private var nextAppointmentId = 100
    private var nextVehicleId = 10

    // ── İlk açılışta çalışan başlangıç kodu ─────────────────────────────────
    init {
        // Demo kullanıcı: uygulamayı test etmek için hazır hesap
        val demoUser = RegisteredUser(
            name        = "Demo Kullanıcı",
            email       = "demo@otokuven.com",
            phone       = "0555 000 0000",
            password    = "demo123",
            accountType = AccountType.CUSTOMER
        )
        demoUser.vehicles.add(
            Vehicle(id = 1, brand = "Renault", model = "Clio 4", year = 2012,
                    plate = "34 ABC 123", fuelType = "Dizel", engine = "1.5 dCi")
        )
        registeredUsers.add(demoUser)
        rebuildAllAppointments()
    }

    // ── Kalıcı veri (SharedPreferences) ─────────────────────────────────────
    private var appContext: Context? = null
    private val gson = Gson()          // JSON dönüşümü için Gson kütüphanesi
    private val PREFS = "al_prefs"    // SharedPreferences dosya adı

    // Uygulamanın başında (AutoLinkApp.onCreate) çağrılır.
    // Kayıtlı kullanıcıları ve aktif oturumu cihazdan okur.
    fun load(context: Context) {
        appContext = context
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Kayıtlı kullanıcıları JSON'dan geri yükle
        val usersJson = prefs.getString("users", null)
        if (!usersJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<ArrayList<RegisteredUser>>() {}.type
                val saved: ArrayList<RegisteredUser> = gson.fromJson(usersJson, type)
                registeredUsers.clear()
                registeredUsers.addAll(saved)
            } catch (_: Exception) {}
        }

        // Kullanıcıların oluşturduğu işletmeleri geri yükle (id >= 100)
        val bizJson = prefs.getString("biz_extra", null)
        if (!bizJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<ArrayList<Business>>() {}.type
                val extra: ArrayList<Business> = gson.fromJson(bizJson, type)
                extra.forEach { biz ->
                    val safeBiz = if (biz.services == null) biz.copy(services = mutableListOf()) else biz
                    if (userCreatedBusinesses.none { it.id == safeBiz.id }) {
                        userCreatedBusinesses.add(safeBiz)
                    }
                }
            } catch (_: Exception) {}
        }

        // Eski app sürümlerinde oluşmuş olabilecek yinelenen ID'leri temizle
        val seenAppt = mutableSetOf<Int>()
        val seenVeh  = mutableSetOf<Int>()
        registeredUsers.forEach { user ->
            user.appointments.removeIf { !seenAppt.add(it.id) }
            user.vehicles.removeIf    { !seenVeh.add(it.id)  }
        }

        rebuildAllAppointments()

        // ID sayaçlarını yüklenen veriye göre sıfırla — yeniden başlatmada çakışma önlenir
        val maxBizId  = userCreatedBusinesses.maxOfOrNull { it.id } ?: 99
        val maxApptId = registeredUsers.flatMap { it.appointments }.maxOfOrNull { it.id } ?: 99
        val maxVehId  = registeredUsers.flatMap { it.vehicles }.maxOfOrNull { it.id } ?: 9
        if (maxBizId  >= nextBusinessId)     nextBusinessId     = maxBizId  + 1
        if (maxApptId >= nextAppointmentId)  nextAppointmentId  = maxApptId + 1
        if (maxVehId  >= nextVehicleId)      nextVehicleId      = maxVehId  + 1

        // Önceki oturum varsa otomatik giriş yap
        val savedEmail = prefs.getString("session_email", "") ?: ""
        if (savedEmail.isNotEmpty()) {
            val user = registeredUsers.find { it.email.equals(savedEmail, ignoreCase = true) }
            if (user != null) {
                userVehicles     = user.vehicles
                userAppointments = user.appointments
                currentUserName    = user.name
                currentUserEmail   = user.email
                currentUserPhone   = user.phone
                currentAccountType = user.accountType
                currentBusinessId  = user.businessId
                isLoggedIn         = true
            }
        }
    }

    // Mevcut durumu SharedPreferences'a JSON olarak yazar.
    // Veri değiştiren her fonksiyonun sonunda çağrılır.
    fun save() {
        val prefs = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE) ?: return
        prefs.edit()
            .putString("users",         gson.toJson(registeredUsers.toList()))
            .putString("biz_extra",     gson.toJson(userCreatedBusinesses.filter { it.id >= 100 }))
            .putString("session_email", if (isLoggedIn) currentUserEmail else "")
            .apply()
    }

    // Yeni kullanıcı kaydeder. E-posta zaten varsa false döner.
    // İşletme hesabıysa aynı anda bir Business nesnesi de oluşturulur.
    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        accountType: AccountType,
        businessName: String = "",
        serviceType: String = "",
        address: String = ""
    ): Boolean {
        if (registeredUsers.any { it.email.equals(email.trim(), ignoreCase = true) }) return false

        var businessId = -1
        if (accountType == AccountType.BUSINESS && businessName.isNotBlank()) {
            val type = if (serviceType == "maintenance") BusinessType.MAINTENANCE else BusinessType.WASHING
            val business = Business(
                id            = nextBusinessId++,
                name          = businessName,
                rating        = 5.0f,
                distanceKm    = 0f,
                distanceText  = "Yeni İşletme",
                startingPrice = if (type == BusinessType.WASHING) 250 else 1200,
                isVerified    = false,
                address       = address.ifBlank { "Adres belirtilmedi" },
                hasValet      = false,
                onSiteService = false,
                isAvailable   = true,
                type          = type,
                phone         = phone
            )
            businessId = business.id
            userCreatedBusinesses.add(business)
        }

        val newUser = RegisteredUser(
            name        = name.trim(),
            email       = email.trim(),
            phone       = phone.trim(),
            password    = password,
            accountType = accountType,
            businessId  = businessId
        )
        registeredUsers.add(newUser)

        // Aktif referansları yeni kullanıcının listelerine yönlendir
        userVehicles    = newUser.vehicles
        userAppointments = newUser.appointments

        currentUserName    = newUser.name
        currentUserEmail   = newUser.email
        currentUserPhone   = newUser.phone
        currentAccountType = accountType
        currentBusinessId  = businessId
        isLoggedIn         = true
        save()
        return true
    }

    // E-posta ve şifre ile giriş yapar. Hatalı bilgide false döner.
    fun login(email: String, password: String): Boolean {
        val user = registeredUsers.find {
            it.email.equals(email.trim(), ignoreCase = true) && it.password == password
        } ?: return false

        // Aktif referansları bu kullanıcının listelerine yönlendir
        userVehicles     = user.vehicles
        userAppointments = user.appointments

        currentUserName    = user.name
        currentUserEmail   = user.email
        currentUserPhone   = user.phone
        currentAccountType = user.accountType
        currentBusinessId  = user.businessId
        isLoggedIn         = true
        save()
        return true
    }

    // Oturumu kapatır. Kullanıcı verisine dokunulmaz, yalnızca aktif referanslar sıfırlanır.
    fun logout() {
        userVehicles     = mutableListOf()
        userAppointments = mutableListOf()

        isLoggedIn         = false
        currentUserName    = ""
        currentUserEmail   = ""
        currentUserPhone   = ""
        currentAccountType = AccountType.CUSTOMER
        currentBusinessId  = -1
        selectedTab        = 0
        save()
    }

    // Aktif kullanıcıya yeni araç ekler ve kaydeder
    fun addVehicle(
        brand: String,
        model: String,
        year: Int,
        plate: String,
        fuelType: String,
        engine: String = ""
    ): Vehicle {
        val vehicle = Vehicle(
            id       = nextVehicleId++,
            brand    = brand,
            model    = model,
            year     = year,
            plate    = plate,
            fuelType = fuelType,
            engine   = engine
        )
        userVehicles.add(vehicle)
        save()
        return vehicle
    }

    // Mevcut aracın bilgilerini günceller (id üzerinden bulunur)
    fun updateVehicle(vehicle: Vehicle) {
        val index = userVehicles.indexOfFirst { it.id == vehicle.id }
        if (index >= 0) { userVehicles[index] = vehicle; save() }
    }

    // Yeni randevu oluşturur ve hem kullanıcının listesine hem allAppointments'a ekler
    fun addAppointment(
        businessName: String,
        serviceName: String,
        date: String,
        time: String,
        price: Int,
        vehicleName: String,
        hasValet: Boolean = false,
        valetAddress: String = "",
        isOnSite: Boolean = false,
        onSiteAddress: String = ""
    ) {
        userAppointments.add(
            Appointment(
                id            = nextAppointmentId++,
                businessName  = businessName,
                serviceName   = serviceName,
                date          = date,
                time          = time,
                totalPrice    = price,
                vehicleName   = vehicleName,
                status        = AppointmentStatus.PENDING,
                hasValet      = hasValet,
                valetAddress  = valetAddress,
                isOnSite      = isOnSite,
                onSiteAddress = onSiteAddress
            )
        )
        val appt = userAppointments.last()
        allAppointments.add(appt)
        save()
    }
}
