package com.kpitb.mustahiq.update.models

data class HospitalDataResponse(
    val data: List<Hospital>
)

data class Hospital(
    val dh_name: String,
    val dh_name_urdu: String,
    val dh_name_pushto: String,
    val dh_focal_person: String,
    val dh_focal_person_urdu: String,
    val dh_focal_person_pushto: String,
    val dh_phone: String,
    val dist_id: String?,        // Nullable since it can be null
    val dist_name: String?,      // Nullable since it can be null
    val dh_latitude: String,
    val dh_longitude: String
)