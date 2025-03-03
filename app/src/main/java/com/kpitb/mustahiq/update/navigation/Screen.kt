package com.kpitb.mustahiq.update.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object Scheme : Screen("scheme")
    data object Search : Screen("search")
    data class SchemeDetails(val schemeName: String) : Screen("scheme_details/{schemeName}") {
        companion object {
            fun createRoute(schemeName: String) = "scheme_details/$schemeName"
        }
    }
    data object Hospital : Screen("hospital")
    data class HospitalDetails(val hospitalName: String) : Screen("hospital_details/{hospitalName}") {
        companion object {
            fun createRoute(hospitalName: String) = "hospital_details/$hospitalName"
        }
    }
    data object DistrictOffice : Screen("district_office")
    data class DistrictOfficeDetails(val districtName: String) : Screen("district_office_details/{districtOfficeName}") {
        companion object {
            fun createRoute(hospitalName: String) = "district_office_details/$hospitalName"
        }
    }
    data class ZakatCommittees(val districtId: String) : Screen("zakat_committees/{districtId}") {
        companion object {
            fun createRoute(districtId: String) = "zakat_committees/$districtId"
        }
    }
    data class ZakatCommitteesDetails(val localCommitteeName: String) : Screen("zakat_committees_details/{localCommitteeName}") {
        companion object {
            fun createRoute(localCommitteeName: String) = "zakat_committees_details/$localCommitteeName"
        }
    }
}