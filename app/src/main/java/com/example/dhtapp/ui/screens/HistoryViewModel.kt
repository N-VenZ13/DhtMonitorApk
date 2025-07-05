package com.example.dhtapp.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dhtapp.data.DhtData
import com.example.dhtapp.api.ApiClient
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import android.util.Log


class HistoryViewModel : ViewModel() {

    private val _historyData = MutableLiveData<List<DhtData>>()
    val historyData: LiveData<List<DhtData>> = _historyData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var updateTimer: Timer? = null

    init {
//        fetchDhtHistory()
    }

    // Fungsi untuk memulai fetching data secara berkala
    fun startFetchingDataPeriodically() {
        Log.d("HistoryViewModel", "Starting periodic data fetch for history.")
        // Hentikan timer yang ada jika ada untuk mencegah duplikasi
        stopFetchingDataPeriodically()

        updateTimer = Timer()
        // Jadwalkan tugas untuk dijalankan setiap 'interval' milidetik
        updateTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Panggil fungsi fetching data
                fetchDhtHistory()
            }
        }, 0, 10000) // Fetch setiap 10 detik (10000 milidetik)
    }

    // Fungsi untuk menghentikan fetching data berkala
    fun stopFetchingDataPeriodically() {
        Log.d("HistoryViewModel", "Stopping periodic data fetch for history.")
        updateTimer?.cancel() // Batalkan timer
        updateTimer = null
    }

    fun fetchDhtHistory() {
        _isLoading.postValue(true)
//        _errorMessage.value = null
        _errorMessage.postValue(null)
        viewModelScope.launch {
            try {
                val response = ApiClient.dhtApiService.getDhtHistory()
                if (response.isSuccessful) {
                    val historyApiResponse = response.body()
                    if (historyApiResponse != null && historyApiResponse.data.isNotEmpty()) {
                        _historyData.value = historyApiResponse.data
                        Log.d("HistoryViewModel", "History data fetched successfully. Count: ${historyApiResponse.data.size}")
                    } else {
                        _errorMessage.value = "No historical data found or invalid response."
                        _historyData.value = emptyList()
                        Log.w("HistoryViewModel", "History API response data is null or empty.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "API Error: ${response.code()} - ${errorBody ?: response.message()}"
                    _historyData.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: ${e.message}"
                _historyData.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        stopFetchingDataPeriodically() // Pastikan timer dibatalkan saat ViewModel dihancurkan
        Log.d("HistoryViewModel", "ViewModel cleared. Timer cancelled.")
    }
}