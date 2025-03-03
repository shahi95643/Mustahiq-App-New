package com.kpitb.mustahiq.update.models

data class DistrictResponse(
    val data: List<District>
)

data class District(
    val dist_id: String,
    val dist_name: String,
    val dist_name_urdu: String,
    val dist_name_pashto: String,
    val dist_officer_name: String,
    val dist_officer_name_urdu: String,
    val dist_officer_name_pashto: String,
    val dist_no_lzc: String,
    val dist_phone: String,
    val dist_chairman_phone: String,
    val dist_chairman_name: String,
    val dist_chairman_name_urdu: String,
    val dist_chairman_name_pashto: String,
    val dist_latitude: String,
    val dist_longitude: String
)