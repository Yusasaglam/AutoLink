package com.yusa.autolink.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yusa.autolink.data.model.AccountType
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.Business
import com.yusa.autolink.data.model.BusinessService
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.data.model.Vehicle

object AppState {
    var isLoggedIn: Boolean = false
    var selectedTab: Int = 0

    var currentUserName: String = ""
    var currentUserEmail: String = ""
    var currentUserPhone: String = ""
    var currentAccountType: AccountType = AccountType.CUSTOMER
    var currentBusinessId: Int = -1

    var lastBusinessName: String = ""
    var lastServiceName: String = ""
    var lastDate: String = ""
    var lastTime: String = ""
    var lastPrice: Int = 0

    // Her kullanıcının kendi araç ve randevu listesi var
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

    val registeredUsers = mutableListOf<RegisteredUser>()
    val userCreatedBusinesses = mutableListOf<Business>()

    // Aktif kullanıcının listelerine doğrudan referans — login/register'da güncellenir
    var userVehicles: MutableList<Vehicle> = mutableListOf()
    var userAppointments: MutableList<Appointment> = mutableListOf()

    // Tüm kullanıcıların randevuları — işletme paneli için
    val allAppointments: MutableList<Appointment> = mutableListOf()

    fun rebuildAllAppointments() {
        allAppointments.clear()
        registeredUsers.forEach { allAppointments.addAll(it.appointments) }
    }

    fun updateAppointmentStatus(appointmentId: Int, newStatus: AppointmentStatus) {
        registeredUsers.forEach { user ->
            val idx = user.appointments.indexOfFirst { it.id == appointmentId }
            if (idx >= 0) user.appointments[idx] = user.appointments[idx].copy(status = newStatus)
        }
        rebuildAllAppointments()
        save()
    }

    fun addBusinessService(name: String, price: Int) {
        val idx = userCreatedBusinesses.indexOfFirst { it.id == currentBusinessId }
        if (idx < 0) return
        val biz = userCreatedBusinesses[idx]
        val services = (biz.services ?: mutableListOf()).toMutableList()
        val nextId = (services.maxOfOrNull { it.id } ?: 0) + 1
        services.add(BusinessService(id = nextId, name = name, price = price))
        val newStartingPrice = services.minOf { it.price }
        userCreatedBusinesses[idx] = biz.copy(startingPrice = newStartingPrice, services = services)
        save()
    }

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

    private var nextBusinessId = 100
    private var nextAppointmentId = 100
    private var nextVehicleId = 10

    init {
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

    private var appContext: Context? = null
    private val gson = Gson()
    private val PREFS = "al_prefs"

    fun load(context: Context) {
        appContext = context
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Restore registered users (if saved)
        val usersJson = prefs.getString("users", null)
        if (!usersJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<ArrayList<RegisteredUser>>() {}.type
                val saved: ArrayList<RegisteredUser> = gson.fromJson(usersJson, type)
                registeredUsers.clear()
                registeredUsers.addAll(saved)
            } catch (_: Exception) {}
        }

        // Restore user-created businesses (id >= 100) — demo businesses already seeded in init
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

        // Deduplicate any IDs that were already duplicated by old app versions
        val seenAppt = mutableSetOf<Int>()
        val seenVeh  = mutableSetOf<Int>()
        registeredUsers.forEach { user ->
            user.appointments.removeIf { !seenAppt.add(it.id) }
            user.vehicles.removeIf    { !seenVeh.add(it.id)  }
        }

        rebuildAllAppointments()

        // Recalculate ID counters from loaded data to prevent duplicate IDs after restart
        val maxBizId  = userCreatedBusinesses.maxOfOrNull { it.id } ?: 99
        val maxApptId = registeredUsers.flatMap { it.appointments }.maxOfOrNull { it.id } ?: 99
        val maxVehId  = registeredUsers.flatMap { it.vehicles }.maxOfOrNull { it.id } ?: 9
        if (maxBizId  >= nextBusinessId)     nextBusinessId     = maxBizId  + 1
        if (maxApptId >= nextAppointmentId)  nextAppointmentId  = maxApptId + 1
        if (maxVehId  >= nextVehicleId)      nextVehicleId      = maxVehId  + 1

        // Restore session
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

    fun save() {
        val prefs = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE) ?: return
        prefs.edit()
            .putString("users",        gson.toJson(registeredUsers.toList()))
            .putString("biz_extra",    gson.toJson(userCreatedBusinesses.filter { it.id >= 100 }))
            .putString("session_email", if (isLoggedIn) currentUserEmail else "")
            .apply()
    }

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

    fun logout() {
        // Aktif referansları boş yeni listelere yönlendir (kullanıcı verisine dokunulmaz)
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

    fun updateVehicle(vehicle: Vehicle) {
        val index = userVehicles.indexOfFirst { it.id == vehicle.id }
        if (index >= 0) { userVehicles[index] = vehicle; save() }
    }

    fun addAppointment(
        businessName: String,
        serviceName: String,
        date: String,
        time: String,
        price: Int,
        vehicleName: String
    ) {
        userAppointments.add(
            Appointment(
                id           = nextAppointmentId++,
                businessName = businessName,
                serviceName  = serviceName,
                date         = date,
                time         = time,
                totalPrice   = price,
                vehicleName  = vehicleName,
                status       = AppointmentStatus.PENDING
            )
        )
        val appt = userAppointments.last()
        allAppointments.add(appt)
        save()
    }
}
