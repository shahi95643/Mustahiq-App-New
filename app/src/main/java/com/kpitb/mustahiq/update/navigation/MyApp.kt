package com.kpitb.mustahiq.update.navigation

import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kpitb.mustahiq.R
import com.kpitb.mustahiq.update.screens.districtoffice.DistrictOfficesDetailsScreen
import com.kpitb.mustahiq.update.screens.districtoffice.DistrictOfficesScreen
import com.kpitb.mustahiq.update.screens.hospital.HospitalDetailsScreen
import com.kpitb.mustahiq.update.screens.hospital.HospitalsScreen
import com.kpitb.mustahiq.update.screens.main.MainScreen
import com.kpitb.mustahiq.update.screens.scheme.SchemeDetailsScreen
import com.kpitb.mustahiq.update.screens.scheme.SchemesScreen
import com.kpitb.mustahiq.update.screens.searchscreen.WebViewScreen
import com.kpitb.mustahiq.update.screens.splash.SplashScreen
import com.kpitb.mustahiq.update.screens.zakatcommittees.ZakatCommitteesScreen
import com.kpitb.mustahiq.update.screens.zakatcommittees.ZakatCommitteesScreenDetails
import com.kpitb.mustahiq.update.ui.theme.ZakatAndUsherTheme
import com.kpitb.mustahiq.update.viewmodel.MustahiqViewModel

@Composable
fun MyApp(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    viewModel: MustahiqViewModel
) {
    val isNightMode by viewModel.isNightMode.collectAsState()
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    ZakatAndUsherTheme(darkTheme = isNightMode) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    viewModel,
                    navController)
            }
            composable(Screen.Main.route) {
                MainScreen(
                    viewModel = viewModel,
                    navController = navController,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                )
            }
            composable(Screen.Scheme.route) {
                SchemesScreen(
                    viewModel = viewModel,
                    navController = navController,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                )
            }
            composable(
                route = Screen.SchemeDetails("schemeName").route,
                arguments = listOf(navArgument("schemeName") { type = NavType.StringType })
            ) { backStackEntry ->
                val schemeName = backStackEntry.arguments?.getString("schemeName")
                val scheme = viewModel.getSchemeByName(schemeName)
                if (scheme != null) {
                    SchemeDetailsScreen(
                        navController = navController,
                        scheme = scheme,
                        viewModel = viewModel
                    )
                }
            }
            composable(Screen.Hospital.route) {
                HospitalsScreen(
                    viewModel = viewModel,
                    navController = navController,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                )
            }
            composable(Screen.DistrictOffice.route) {
                DistrictOfficesScreen(
                    viewModel = viewModel,
                    navController = navController,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                )
            }
            composable(
                route = Screen.HospitalDetails("hospitalName").route,
                arguments = listOf(navArgument("hospitalName") { type = NavType.StringType })
            ) { backStackEntry ->
                val hospitalName = backStackEntry.arguments?.getString("hospitalName")
                val hospital = viewModel.getHospitalByName(hospitalName) // Fetch hospital by ID
                if (hospital != null) {
                    HospitalDetailsScreen(
                        navController = navController,
                        hospital = hospital,
                        viewModel = viewModel
                    )
                }
            }
            composable(
                route = Screen.DistrictOfficeDetails("districtOfficeName").route,
                arguments = listOf(navArgument("districtOfficeName") { type = NavType.StringType })
            ) { backStackEntry ->
                val districtOfficeName = backStackEntry.arguments?.getString("districtOfficeName")
                val districtName = viewModel.getDistrictByName(districtOfficeName) // Fetch hospital by ID
                if (districtName != null) {
                    DistrictOfficesDetailsScreen(
                        navController = navController,
                        districtName = districtName,
                        viewModel = viewModel
                    )
                }
            }
            composable(
                route = Screen.ZakatCommittees("districtId").route,
                arguments = listOf(navArgument("districtId") { type = NavType.StringType })
            ) { backStackEntry ->
                val districtId = backStackEntry.arguments?.getString("districtId") ?: ""
                ZakatCommitteesScreen(
                    navController = navController,
                    districtId = districtId,
                    viewModel = viewModel
                )
            }
            composable(Screen.Search.route) { WebViewScreen(navController) }
            composable(
                route = Screen.ZakatCommitteesDetails("localCommitteeName").route,
                arguments = listOf(navArgument("localCommitteeName") { type = NavType.StringType })
            ) { backStackEntry ->
                val localCommitteeName = backStackEntry.arguments?.getString("localCommitteeName")
                val localCommittee = viewModel.getLocalCommitteeByName(localCommitteeName)
                val tehsilName = when (currentLanguage) {
                    "ur" -> localCommittee?.tehsil_name
                    "ps" -> localCommittee?.tehsil_name
                    else -> localCommittee?.tehsil_name
                }
                if (!tehsilName.isNullOrEmpty() && tehsilName.isNotEmpty()) {
                    if (localCommittee != null) {
                        ZakatCommitteesScreenDetails(
                            navController = navController,
                            tehsil = localCommittee,
                            viewModel = viewModel
                        )
                    }
                } else {
                    val msg = stringResource(id = R.string.committee_not_found_or_data_unavailable)
                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}