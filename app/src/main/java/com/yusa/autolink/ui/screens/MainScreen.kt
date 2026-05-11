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

// ============================================================
// MainScreen — Müşteri tarafı alt menü sarmalayıcısı
//
// Bu ekran kendi başına içerik göstermez; sadece alt menü
// (NavigationBar) ile 4 sekme arasında geçiş sağlar.
// Her sekme bağımsız bir @Composable ekrandır:
//   0 → HomeScreen       (Ana Sayfa)
//   1 → MyAppointmentsScreen (Randevularım)
//   2 → MyVehiclesScreen (Araçlarım)
//   3 → ProfileScreen    (Profil)
//
// AppState.selectedTab kullanımı:
//   Başka ekranlar (örn. AppointmentSuccessScreen) bu değeri
//   önceden set eder; MainScreen açıldığında doğru sekme aktif olur.
//   Örneğin "Randevularımı Gör" butonuna basıldığında selectedTab = 1
//   yapılır, ardından MAIN route'una navigate edilir.
// ============================================================
@Composable
fun MainScreen(
    onNavigateToBusinessList: (String) -> Unit, // HomeScreen'den "washing"/"maintenance" ile çağrılır
    onNavigateToLogin:        () -> Unit         // Profil sekmesinde çıkış yapıldığında
) {
    // AppState.selectedTab'dan başlangıç değerini alır.
    // remember { } ile sadece ilk render'da AppState okunur;
    // kullanıcı sekme değiştirince yerel state güncellenir.
    var selectedTab by remember { mutableIntStateOf(AppState.selectedTab) }

    Scaffold(
        bottomBar = {
            // NavigationBar → Material 3 alt menü bileşeni
            // Her NavigationBarItem tıklanınca selectedTab değişir,
            // when(selectedTab) bloğu yeni ekranı çizer (recompose)
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
        // Box içinde sadece aktif sekmenin ekranı çizilir (diğerleri bellekte tutulmaz)
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                // onAddVehicle → Araçlarım sekmesine geçer (sekme = 2)
                0 -> HomeScreen(
                    onNavigateToBusinessList = onNavigateToBusinessList,
                    onAddVehicle             = { selectedTab = 2 }
                )
                1 -> MyAppointmentsScreen()
                2 -> MyVehiclesScreen()
                // Çıkış yapıldığında AppState.logout() çağrılır, ardından Login'e gidilir
                3 -> ProfileScreen(onLogout = {
                    AppState.logout()
                    onNavigateToLogin()
                })
            }
        }
    }
}
