package com.example.warsan.network

import com.example.warsan.models.GuardianResponse
import com.example.warsan.models.LogInRequest
import com.example.warsan.models.LogInResponse
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

}