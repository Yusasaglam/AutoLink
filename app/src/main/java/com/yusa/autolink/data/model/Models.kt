package com.yusa.autolink.data.model

data class Vehicle(
    val id: String,
    val name: String,
    val plate: String,
    val year: Int,
    val fuelLevel: Int,
    val mileage: Int
)

data class ServiceCenter(
    val id: String,
    val name: String,
    val address: String,
    val rating: Float,
    val reviewCount: Int,
    val distance: String,
    val isOpen: Boolean,
    val services: List<String>
)

data class ActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val date: String,
    val type: ActivityType
)

enum class ActivityType { SERVICE, APPOINTMENT, DOCUMENT, ALERT }

enum class ButtonVariant { Primary, Outline, Ghost }

enum class ChipType { Info, Warning, Success, Error }
