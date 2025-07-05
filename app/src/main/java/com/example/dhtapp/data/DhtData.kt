package com.example.dhtapp.data

import com.google.gson.annotations.SerializedName

data class DhtData(
    @SerializedName("id") val id: Int,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("humidity") val humidity: Float,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class DhtApiResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DhtData?
)

// Data class untuk membungkus respons array dari history
data class DhtHistoryApiResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<DhtData> // List of DhtData objects
)

