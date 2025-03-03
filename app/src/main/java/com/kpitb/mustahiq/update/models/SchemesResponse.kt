package com.kpitb.mustahiq.update.models

data class SchemesResponse(
    val data: List<Scheme>,
    val success: Int
)

data class Scheme(
    val scheme_id: String,
    val scheme_title: String,
    val scheme_title_urdu: String,
    val scheme_description: String,
    val scheme_description_urdu: String,
    val scheme_eligibility_criteria: String,
    val scheme_eligibility_criteria_urdu: String,
    val scheme_apply_instruction: String,
    val scheme_apply_instruction_urdu: String,
    val file_path: String,
    val scheme_status: String
)
