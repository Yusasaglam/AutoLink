package com.yusa.autolink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.AccountType
import com.yusa.autolink.ui.screens.*

// ============================================================
// AppNavigation.kt — Tüm ekran geçişleri tek yerden yönetilir
//
// Jetpack Compose Navigation kullanır. Her ekranın bir "route"
// (yol/adres) vardır; tıpkı web'deki URL gibi. Ekranlar arası
// geçiş navController.navigate("route") ile yapılır.
//
// Navigasyon akışı:
//   Splash → (oturum varsa) MAIN veya BUSINESS_MAIN
//          → (yoksa)        LOGIN
//   LOGIN  → MAIN veya BUSINESS_MAIN
//   MAIN   → BUSINESS_LIST → APPOINTMENT → APPOINTMENT_SUCCESS
// ============================================================

// Tüm route string'leri sabit olarak burada tanımlandı.
// String yazmak yerine Routes.MAIN diye kullanmak,
// yazım hatalarını derleme aşamasında yakalar.
object Routes {
    const val SPLASH              = "splash"
    const val LOGIN               = "login"
    const val REGISTER            = "register"
    const val MAIN                = "main"           // Müşteri ana ekranı (alt menü)
    const val BUSINESS_MAIN      = "business_main"  // İşletme sahibi paneli
    const val BUSINESS_LIST       = "business_list"
    const val APPOINTMENT         = "appointment"
    const val APPOINTMENT_SUCCESS = "appointment_success"
}

@Composable
fun AppNavigation() {
    // NavController → ekranlar arası geçişi ve geri stack'ini yöneten nesne
    val navController = rememberNavController()

    // NavHost → hangi route'da hangi ekranın açılacağını tanımlar
    // startDestination → uygulama açıldığında ilk gösterilecek ekran
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // 1. Splash — animasyonlu açılış ekranı
        // Oturum açıksa kullanıcı tipine göre doğrudan panele gider (login ekranı atlanır).
        // popUpTo(inclusive=true) → Splash geri stack'ten çıkarılır; geri tuşu Login'e dönmez.
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = {
                    if (AppState.isLoggedIn) {
                        val dest = if (AppState.currentAccountType == AccountType.BUSINESS)
                            Routes.BUSINESS_MAIN else Routes.MAIN
                        navController.navigate(dest) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 2. Giriş ekranı — e-posta + şifre doğrulama
        // Başarılı girişte hesap tipine göre MAIN veya BUSINESS_MAIN'e gider.
        // popUpTo(LOGIN, inclusive=true) → Login, geri stack'ten kaldırılır.
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    val dest = if (AppState.currentAccountType == AccountType.BUSINESS)
                        Routes.BUSINESS_MAIN else Routes.MAIN
                    AppState.selectedTab = 0
                    navController.navigate(dest) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // 3. Kayıt ekranı — müşteri veya işletme hesabı açma
        // Başarılı kayıtta yine hesap tipine göre yönlendirme yapılır.
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToHome = {
                    val dest = if (AppState.currentAccountType == AccountType.BUSINESS)
                        Routes.BUSINESS_MAIN else Routes.MAIN
                    AppState.selectedTab = 0
                    navController.navigate(dest) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4a. Müşteri ana ekranı — alt menülü sarmalayıcı (Home/Randevular/Araçlar/Profil)
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToBusinessList = { serviceType ->
                    // "washing" veya "maintenance" parametresi URL'e eklenerek geçirilir
                    navController.navigate("${Routes.BUSINESS_LIST}/$serviceType")
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // 4b. İşletme sahibi paneli — Randevular / Ücretlendirme / İşletmem sekmeleri
        composable(Routes.BUSINESS_MAIN) {
            BusinessMainScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.BUSINESS_MAIN) { inclusive = true }
                    }
                }
            )
        }

        // 5. İşletme listesi — URL parametresi olarak serviceType alır
        // Örnek route: "business_list/washing"
        // navArgument → parametrenin türünü tanımlar (String); null gelmez.
        composable(
            route     = "${Routes.BUSINESS_LIST}/{serviceType}",
            arguments = listOf(navArgument("serviceType") { type = NavType.StringType })
        ) { backStackEntry ->
            // backStackEntry.arguments → URL'deki parametreleri okur
            val serviceType = backStackEntry.arguments?.getString("serviceType") ?: "washing"
            BusinessListScreen(
                serviceType             = serviceType,
                onNavigateToAppointment = { businessId ->
                    // İşletme ID'si ve hizmet tipi sonraki ekrana URL ile taşınır
                    navController.navigate("${Routes.APPOINTMENT}/$businessId/$serviceType")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Randevu oluşturma ekranı — businessId (Int) + serviceType (String) alır
        // Int ve String farklı navArgument tipleri: NavType.IntType / StringType
        composable(
            route     = "${Routes.APPOINTMENT}/{businessId}/{serviceType}",
            arguments = listOf(
                navArgument("businessId")  { type = NavType.IntType    },
                navArgument("serviceType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val businessId  = backStackEntry.arguments?.getInt("businessId")     ?: 1
            val serviceType = backStackEntry.arguments?.getString("serviceType") ?: "washing"
            AppointmentScreen(
                businessId          = businessId,
                serviceType         = serviceType,
                onNavigateToSuccess = {
                    navController.navigate(Routes.APPOINTMENT_SUCCESS) {
                        // Başarı ekranından geri gidilirse MAIN'e dönülür (Appointment/BusinessList atlanır)
                        popUpTo(Routes.MAIN)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Randevu başarı ekranı — randevu bilgileri AppState'teki lastXxx alanlarından okunur
        // Bu ekrandan MAIN'e gidildiğinde selectedTab ile hangi sekmenin açılacağı belirlenir.
        composable(Routes.APPOINTMENT_SUCCESS) {
            AppointmentSuccessScreen(
                businessName = AppState.lastBusinessName,
                serviceName  = AppState.lastServiceName,
                date         = AppState.lastDate,
                time         = AppState.lastTime,
                price        = AppState.lastPrice,
                onNavigateToMyAppointments = {
                    // selectedTab = 1 → Randevularım sekmesi açılır
                    AppState.selectedTab = 1
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // selectedTab = 0 → Ana Sayfa sekmesi açılır
                    AppState.selectedTab = 0
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
