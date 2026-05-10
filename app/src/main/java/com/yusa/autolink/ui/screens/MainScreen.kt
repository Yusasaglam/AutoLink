package com.yusa.autolink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.yusa.autolink.data.AppState

// Ana ekran sarmalayıcı - alt menü navigasyonunu yönetir.
// 4 sekme: Ana Sayfa, Randevularım, Araçlarım, Profil
@Composable
fun MainScreen(
    onNavigateToBusinessList: (String) -> Unit,
    onNavigateToLogin:        () -> Unit
) {
    // Hangi sekme seçili? AppState'ten başlangıç değerini al.
    // "Randevularımı Gör" gibi dış navigasyonlar selectedTab'ı önceden ayarlar.
    var selectedTab by remember { mutableIntStateOf(AppState.selectedTab) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    icon     = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label    = { Text("Ana Sayfa") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    icon     = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    label    = { Text("Randevularım") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick  = { selectedTab = 2 },
                    icon     = { Icon(Icons.Filled.DirectionsCar, contentDescription = null) },
                    label    = { Text("Araçlarım") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick  = { selectedTab = 3 },
                    icon     = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label    = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToBusinessList = onNavigateToBusinessList,
                    onAddVehicle             = { selectedTab = 2 }
                )
                1 -> MyAppointmentsScreen()
                2 -> MyVehiclesScreen()
                3 -> ProfileScreen(onLogout = {
                    AppState.logout()
                    onNavigateToLogin()
                })
            }
        }
    }
}
