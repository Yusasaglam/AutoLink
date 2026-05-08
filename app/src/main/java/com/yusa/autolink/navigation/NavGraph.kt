package com.yusa.autolink.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yusa.autolink.data.model.BusinessProfile
import com.yusa.autolink.data.model.ServiceCenter
import com.yusa.autolink.data.model.UserType
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.screens.appointments.AppointmentsScreen
import com.yusa.autolink.ui.screens.auth.LoginScreen
import com.yusa.autolink.ui.screens.auth.RegisterScreen
import com.yusa.autolink.ui.screens.auth.UserTypeScreen
import com.yusa.autolink.ui.screens.dashboard.DashboardScreen
import com.yusa.autolink.ui.screens.documents.DocumentsScreen
import com.yusa.autolink.ui.screens.listing.ListingScreen
import com.yusa.autolink.ui.screens.profile.ProfileScreen
import com.yusa.autolink.ui.screens.serviceprovider.ProviderAppointmentsScreen
import com.yusa.autolink.ui.screens.serviceprovider.ServiceProviderDashboardScreen
import com.yusa.autolink.ui.screens.serviceprovider.ServiceProviderSetupScreen
import com.yusa.autolink.ui.screens.vehicle.MyVehiclesScreen
import com.yusa.autolink.ui.screens.vehicle.VehicleSetupScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object UserType : Screen("user_type")
    data object Register : Screen("register")
    data object VehicleSetup : Screen("vehicle_setup/{from}") {
        fun createRoute(from: String) = "vehicle_setup/$from"
    }
    data object ServiceProviderSetup : Screen("provider_setup")
    data object Dashboard : Screen("dashboard")
    data object ProviderDashboard : Screen("provider_dashboard")
    data object MyVehicles : Screen("my_vehicles")
    data object Listing : Screen("listing")
    data object Appointments : Screen("appointments")
    data object ProviderAppointments : Screen("provider_appointments")
    data object Documents : Screen("documents")
    data object Profile : Screen("profile")
}

@Composable
fun AutoLinkNavGraph(navController: NavHostController) {
    var userType by remember { mutableStateOf(UserType.VEHICLE_OWNER) }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var businessProfile by remember { mutableStateOf<BusinessProfile?>(null) }
    var providerCenters by remember { mutableStateOf<List<ServiceCenter>>(emptyList()) }

    val primaryVehicleName = vehicles.firstOrNull()?.displayName ?: ""

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.UserType.route) },
                onNavigateToDashboard = {
                    val dest = if (userType == UserType.SERVICE_PROVIDER && businessProfile != null)
                        Screen.ProviderDashboard.route else Screen.Dashboard.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.UserType.route) {
            UserTypeScreen(
                onSelectType = { selectedType ->
                    userType = selectedType
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                userType = userType,
                onNavigateBack = { navController.popBackStack() },
                onRegisterComplete = { name, email, phone ->
                    userName = name
                    userEmail = email
                    userPhone = phone
                    when (userType) {
                        UserType.VEHICLE_OWNER -> navController.navigate(
                            Screen.VehicleSetup.createRoute("register")
                        ) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        UserType.SERVICE_PROVIDER -> navController.navigate(
                            Screen.ServiceProviderSetup.route
                        ) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.VehicleSetup.route,
            arguments = listOf(navArgument("from") { type = NavType.StringType })
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: "dashboard"
            VehicleSetupScreen(
                onVehicleSaved = { vehicle ->
                    vehicles = vehicles + vehicle
                    when (from) {
                        "register" -> navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        else -> navController.popBackStack()
                    }
                },
                onSkip = if (from == "register") ({
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }) else null
            )
        }

        composable(Screen.ServiceProviderSetup.route) {
            ServiceProviderSetupScreen(
                ownerName = userName,
                ownerPhone = userPhone,
                onSetupComplete = { profile ->
                    businessProfile = profile
                    providerCenters = providerCenters + profile.toServiceCenter()
                    navController.navigate(Screen.ProviderDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                userName = userName,
                vehicles = vehicles,
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToMyVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) },
                onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onAddVehicle = { navController.navigate(Screen.VehicleSetup.createRoute("dashboard")) }
            )
        }

        composable(Screen.ProviderDashboard.route) {
            val profile = businessProfile ?: BusinessProfile(
                id = "0", businessName = userName.ifBlank { "Servisim" },
                ownerName = userName, address = "", phone = "", services = emptyList(), isAuthorized = false
            )
            ServiceProviderDashboardScreen(
                businessProfile = profile,
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToProviderAppointments = { navController.navigate(Screen.ProviderAppointments.route) },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) }
            )
        }

        composable(Screen.ProviderAppointments.route) {
            ProviderAppointmentsScreen(
                businessProfile = businessProfile,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.ProviderDashboard.route) {
                        launchSingleTop = true
                        popUpTo(Screen.ProviderDashboard.route)
                    }
                },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.MyVehicles.route) {
            MyVehiclesScreen(
                vehicles = vehicles,
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onAddVehicle = { navController.navigate(Screen.VehicleSetup.createRoute("my_vehicles")) }
            )
        }

        composable(Screen.Listing.route) {
            ListingScreen(
                providerCenters = providerCenters,
                onNavigateToDashboard = { navController.popBackStack() },
                onNavigateToMyVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) }
            )
        }

        composable(Screen.Appointments.route) {
            AppointmentsScreen(
                vehicleName = primaryVehicleName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onNavigateToMyVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Documents.route) {
            DocumentsScreen(
                vehicleName = primaryVehicleName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onNavigateToMyVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                userName = userName,
                userEmail = userEmail,
                userPhone = userPhone,
                userType = userType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    val dest = if (userType == UserType.SERVICE_PROVIDER && businessProfile != null)
                        Screen.ProviderDashboard.route else Screen.Dashboard.route
                    navController.navigate(dest) {
                        launchSingleTop = true
                        popUpTo(dest)
                    }
                },
                onNavigateToMyVehicles = { navController.navigate(Screen.MyVehicles.route) },
                onNavigateToListing = { navController.navigate(Screen.Listing.route) },
                onNavigateToProviderAppointments = { navController.navigate(Screen.ProviderAppointments.route) },
                onLogout = {
                    userName = ""
                    userEmail = ""
                    userPhone = ""
                    vehicles = emptyList()
                    businessProfile = null
                    providerCenters = emptyList()
                    userType = UserType.VEHICLE_OWNER
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
