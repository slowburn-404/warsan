package com.example.warsan.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://infinite-plains-75600-ea6e23941260.herokuapp.com/"


    val instance: Retrofit by lazy {

        // Create an HttpLoggingInterceptor for logging network requests and responses
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY // You can adjust the log level as needed

        // Create an OkHttpClient with the logging interceptor and a custom interceptor for the "Connection: close" header
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Connection", "close")
                    .build()
                chain.proceed(request)
            }.build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
}