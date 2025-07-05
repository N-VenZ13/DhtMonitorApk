package com.example.dhtapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// IMPORTANT: Replace with your Laravel backend IP address and port
// Emulator: "http://10.0.2.2:8000/api/"
// Physical Device: "http://YOUR_LARAVEL_IP_ADDRESS:8000/api/"
const val BASE_URL = "http://192.168.38.157:8000/api/"

object ApiClient {
    val dhtApiService: DhtApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DhtApi::class.java)
    }
}