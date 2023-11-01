package com.example.warsan.network

import com.example.warsan.models.LogInRequest
import com.example.warsan.models.LogInResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WarsanAPI {
    @POST("api/healthworker/login/")
    fun login(@Body requestBody: LogInRequest): Call<LogInResponse>

}