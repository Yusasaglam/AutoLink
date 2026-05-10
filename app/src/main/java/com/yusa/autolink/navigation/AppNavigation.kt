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

// Tüm ekran rota isimleri tek yerde - navigasyonu takip etmek kolaylaşır
object Routes {
    const val SPLASH              = "splash"
    const val LOGIN               = "login"
    const val REGISTER            = "register"
    const val MAIN                = "main"           // Müşteri ekranı
    const val BUSINESS_MAIN      = "business_main"  // İşletme paneli
    const val BUSINESS_LIST       = "business_list"
    const val APPOINTMENT         = "appointment"
    const val APPOINTMENT_SUCCESS = "appointment_success"
}

// Ana navigasyon yapısı - tüm ekran geçişleri burada tanımlanır
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // 1. Splash — oturum varsa direkt panele git, yoksa login'e
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

        // 2. Giriş ekranı
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

        // 3. Kayıt ekranı
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

        // 4. Müşteri ana ekranı
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToBusinessList = { serviceType ->
                    navController.navigate("${Routes.BUSINESS_LIST}/$serviceType")
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // 4b. İşletme paneli
        composable(Routes.BUSINESS_MAIN) {
            BusinessMainScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.BUSINESS_MAIN) { inclusive = true }
                    }
                }
            )
        }

        // 5. İşletme listesi - serviceType: "washing" veya "maintenance"
        composable(
            route     = "${Routes.BUSINESS_LIST}/{serviceType}",
            arguments = listOf(navArgument("serviceType") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceType = backStackEntry.arguments?.getString("serviceType") ?: "washing"
            BusinessListScreen(
                serviceType             = serviceType,
                onNavigateToAppointment = { businessId ->
                    navController.navigate("${Routes.APPOINTMENT}/$businessId/$serviceType")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Randevu oluşturma - businessId + serviceType URL'den gelir
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
                        popUpTo(Routes.MAIN)    // Geri tuşu MAIN'e döner
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Randevu başarı ekranı - AppState'ten randevu bilgilerini okur
        composable(Routes.APPOINTMENT_SUCCESS) {
            AppointmentSuccessScreen(
                businessName = AppState.lastBusinessName,
                serviceName  = AppState.lastServiceName,
                date         = AppState.lastDate,
                time         = AppState.lastTime,
                price        = AppState.lastPrice,
                onNavigateToMyAppointments = {
                    // MAIN'i tazele ve Randevularım sekmesini aç (sekme=1)
                    AppState.selectedTab = 1
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // MAIN'i tazele ve Ana Sayfa sekmesini aç (sekme=0)
                    AppState.selectedTab = 0
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
