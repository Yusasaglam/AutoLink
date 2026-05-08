package com.yusa.autolink.data.model

enum class UserType { VEHICLE_OWNER, SERVICE_PROVIDER }

data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val plate: String,
    val year: Int,
    val fuelLevel: Int = 0,
    val mileage: Int = 0
) {
    val displayName: String get() = "$brand $model"

    val formattedMileage: String get() {
        if (mileage <= 0) return "—"
        return buildString {
            val s = mileage.toString()
            s.forEachIndexed { i, c ->
                if (i > 0 && (s.length - i) % 3 == 0) append('.')
                append(c)
            }
            append(" km")
        }
    }
}

data class BusinessProfile(
    val id: String,
    val businessName: String,
    val ownerName: String,
    val address: String,
    val phone: String,
    val services: List<String>,
    val isAuthorized: Boolean
) {
    fun toServiceCenter() = ServiceCenter(
        id = id,
        name = businessName,
        address = address.ifBlank { "Adres belirtilmedi" },
        rating = 0f,
        reviewCount = 0,
        distance = "—",
        distanceKm = Float.MAX_VALUE,
        isOpen = true,
        isAuthorized = isAuthorized,
        services = services,
        isNew = true
    )
}

data class ServiceCenter(
    val id: String,
    val name: String,
    val address: String,
    val rating: Float,
    val reviewCount: Int,
    val distance: String,
    val distanceKm: Float,
    val isOpen: Boolean,
    val isAuthorized: Boolean,
    val services: List<String>,
    val isNew: Boolean = false
)

data class ActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val date: String,
    val type: ActivityType
)

data class Appointment(
    val id: String,
    val vehicleName: String,
    val serviceName: String,
    val serviceCenter: String,
    val date: String,
    val time: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus { PENDING, UPCOMING, COMPLETED, CANCELLED }
enum class ActivityType { SERVICE, APPOINTMENT, DOCUMENT, ALERT }
enum class ButtonVariant { Primary, Outline, Ghost }
enum class ChipType { Info, Warning, Success, Error }
