package com.kpitb.mustahiq.update.models

data class TehsilResponse(
    val success: Int,
    val data: List<Tehsil>
)

data class Tehsil(
    val tehsil_id: String,
    val tehsil_name: String,
    val tehsil_name_urdu: String,
    val tehsil_name_pashto: String,
    val lzc_chairman: String,
    val lzc_chairman_urdu: String,
    val lzc_chairman_pashto: String,
    val lzc_name: String,
    val lzc_name_urdu: String,
    val lzc_name_pashto: String,
    val lzc_phone: String
)

