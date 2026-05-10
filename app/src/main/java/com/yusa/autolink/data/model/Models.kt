package com.yusa.autolink.data.model

enum class AccountType { CUSTOMER, BUSINESS }
enum class BusinessType { WASHING, MAINTENANCE }
enum class AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }

data class User(val name: String, val phone: String, val email: String)

data class Vehicle(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val fuelType: String,
    val engine: String = ""
)

data class OnboardingPage(val title: String, val description: String, val iconName: String)

data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val averagePrice: Int,
    val duration: String,
    val iconName: String
)

data class BusinessService(
    val id: Int    = 0,
    val name: String = "",
    val price: Int = 0
)

data class Business(
    val id: Int,
    val name: String,
    val rating: Float,
    val distanceKm: Float,
    val distanceText: String,
    val startingPrice: Int,
    val isVerified: Boolean,
    val address: String,
    val hasValet: Boolean,
    val onSiteService: Boolean,
    val isAvailable: Boolean,
    val type: BusinessType,
    val phone: String = "",
    val services: MutableList<BusinessService> = mutableListOf()
)

data class Appointment(
    val id: Int,
    val businessName: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val totalPrice: Int,
    val vehicleName: String,
    val status: AppointmentStatus
)
