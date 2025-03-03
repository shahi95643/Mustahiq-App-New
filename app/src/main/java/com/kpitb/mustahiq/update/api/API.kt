package com.kpitb.mustahiq.update.api

import com.kpitb.mustahiq.update.models.DistrictResponse
import com.kpitb.mustahiq.update.models.HospitalDataResponse
import com.kpitb.mustahiq.update.models.SchemesResponse
import com.kpitb.mustahiq.update.models.TehsilResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("mustahiq/API/activeSchemes")
    suspend fun getActiveSchemes(): Response<SchemesResponse>

    @GET("mustahiq/API/districtOffices")
    suspend fun getDistrictOffices(): Response<DistrictResponse>

    @GET("mustahiq/API/districtHospitals")
    suspend fun getHospitalsData(): Response<HospitalDataResponse>

    @GET("mustahiq/API/localZakatCommitte")
    suspend fun getLocalZakatCommittees(@Query("dist_id") distId: String): Response<TehsilResponse>
}