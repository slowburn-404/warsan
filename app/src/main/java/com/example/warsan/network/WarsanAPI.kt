package com.example.warsan.network

import com.example.warsan.models.AddChildRequest
import com.example.warsan.models.AddChildResponse
import com.example.warsan.models.AddGuardianRequest
import com.example.warsan.models.GuardianResponse
import com.example.warsan.models.Location
import com.example.warsan.models.LogInRequest
import com.example.warsan.models.LogInResponse
import com.example.warsan.models.SuccessResponse
import com.example.warsan.models.VaccineData
import com.example.warsan.models.Vaccines
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WarsanAPI {
    @POST("api/healthworker/login/")
    fun login(@Body requestBody: LogInRequest): Call<LogInResponse>
    @GET("api/guardian/{phone_number}")
    fun retrieveGuardian(@Path ("phone_number") phoneNumber: String?): Call<GuardianResponse>
    @POST("api/guardians/")
    fun registerGuardian(@Body guardianRequestBody: AddGuardianRequest): Call<SuccessResponse>
    @GET("api/locations")
    fun getLocations(): Call<List<Location>>
    @POST("api/children/")
    fun addChild(@Body childRequestBody: AddChildRequest): Call<AddChildResponse>
    @GET("api/vaccines/")
    fun getVaccines(): Call<List<Vaccines>>
    @POST("api/immunization-records/")
    fun addRecords(@Body addRecordRequestBody: VaccineData): Call<VaccineData>

}