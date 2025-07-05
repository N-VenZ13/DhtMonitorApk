package com.example.dhtapp.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dhtapp.api.ApiClient
import com.example.dhtapp.data.DhtData
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class MainViewModel : ViewModel() {

    private val _temperature = MutableLiveData<String>()
    val temperature: LiveData<String> = _temperature

    private val _humidity = MutableLiveData<String>()
    val humidity: LiveData<String> = _humidity

    private val _lastUpdated = MutableLiveData<String>()
    val lastUpdated: LiveData<String> = _lastUpdated

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var updateTimer: Timer? = null

    init {
        // Initial fetch when ViewModel is created
        fetchLatestDhtData()
    }

    fun startFetchingDataPeriodically() {
        // Stop any existing timer to prevent duplicates
        stopFetchingDataPeriodically()

        updateTimer = Timer()
        updateTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetchLatestDhtData()
            }
        }, 0, 5000) // Fetch every 5 seconds (5000 milliseconds)
    }

    fun stopFetchingDataPeriodically() {
        updateTimer?.cancel()
        updateTimer = null
    }

    fun fetchLatestDhtData() {
        _errorMessage.postValue(null) // Clear previous errors
        viewModelScope.launch {
            try {
                val response = ApiClient.dhtApiService.getLatestDhtData()
                if (response.isSuccessful) {
                    val dhtApiResponse = response.body()
                    if (dhtApiResponse != null && dhtApiResponse.data != null) {
                        val data = dhtApiResponse.data
                        _temperature.postValue("${String.format("%.1f", data.temperature)} °C")
                        _humidity.postValue("${String.format("%.1f", data.humidity)} %")
                        _lastUpdated.postValue("Last Updated: ${data.createdAt.substring(0, 19).replace("T", " ")}")
                    } else {
                        _errorMessage.postValue("No data available or invalid response format.")
                        clearDisplayData()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue("API Error: ${response.code()} - ${errorBody ?: response.message()}")
                    clearDisplayData()
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Network Error: ${e.message}")
                clearDisplayData()
            }
        }
    }

    private fun clearDisplayData() {
        _temperature.postValue("N/A °C")
        _humidity.postValue("N/A %")
        _lastUpdated.postValue("Last Updated: --:--:--")
    }

    override fun onCleared() {
        super.onCleared()
        stopFetchingDataPeriodically() // Ensure timer is cancelled when ViewModel is destroyed
    }
}