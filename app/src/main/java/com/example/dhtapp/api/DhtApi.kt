package com.example.dhtapp.api

import com.example.dhtapp.data.DhtApiResponse
import com.example.dhtapp.data.DhtHistoryApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface DhtApi {
    @GET("dht/latest")
    suspend fun getLatestDhtData(): Response<DhtApiResponse>

    @GET("dht/history")
    suspend fun getDhtHistory(): Response<DhtHistoryApiResponse> // Tambah ini

}