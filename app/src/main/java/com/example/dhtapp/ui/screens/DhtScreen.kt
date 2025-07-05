package com.example.dhtapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Import ini jika belum ada
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dhtapp.R
import com.example.dhtapp.ui.component.SensorGauge
import com.example.dhtapp.ui.theme.DhtAppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DhtScreen(
    viewModel: MainViewModel = viewModel(),
    onViewHistoryClick: () -> Unit
) {
    // Observe LiveData from ViewModel
    // Pastikan nama variabelnya konsisten agar mudah dibaca
    val temperatureString by viewModel.temperature.observeAsState("N/A °C")
    val humidityString by viewModel.humidity.observeAsState("N/A %")
    val lastUpdated by viewModel.lastUpdated.observeAsState("Last Updated: --:--:--")
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Parsing string ke float untuk gauge
    // Gunakan nilai yang diobservasi dari LiveData
//    val tempValue = temperatureString.replace(" °C", ".").toFloatOrNull() ?: 0f
//    val humValue = humidityString.replace(" %", ".").toFloatOrNull() ?: 0f

    // Gunakan NumberFormat untuk parsing float secara locale-independent
    val numberFormat = remember { NumberFormat.getInstance(Locale.US) } // Pastikan Locale.US untuk titik desimal

    val tempValue = remember(temperatureString) {
        val cleanTempString = temperatureString.replace(" °C", "").replace(",", ".")
        try {
            numberFormat.parse(cleanTempString)?.toFloat() ?: 0f
        } catch (e: Exception) {
            0f // Default value jika parsing gagal
        }
    }

    val humValue = remember(humidityString) {
        val cleanHumString = humidityString.replace(" %", "").replace(",", ".")
        try {
            numberFormat.parse(cleanHumString)?.toFloat() ?: 0f
        } catch (e: Exception) {
            0f // Default value jika parsing gagal
        }
    }

    // --- MULAI DARI SINI: INI ADALAH COLUMN UTAMA YANG MEMBUNGKUS SEMUA ELEMEN UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Padding untuk seluruh layar
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Atur vertikal dari atas
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Judul Aplikasi
        Text(
            text = "DHT22 Monitor",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
        )

        // Row untuk menampung kedua SensorGauge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gauge Suhu
            SensorGauge(
                value = tempValue,
                minValue = 0f,
                maxValue = 100f, // Sesuaikan rentang suhu yang diharapkan
                unit = "°C",
                label = "Suhu",
                modifier = Modifier.weight(1f) // Agar mengambil ruang yang sama
            )
            // Gauge Kelembaban
            SensorGauge(
                value = humValue,
                minValue = 0f,
                maxValue = 100f, // Rentang kelembaban selalu 0-100%
                unit = "%",
                label = "Kelembaban",
                modifier = Modifier.weight(1f), // Agar mengambil ruang yang sama
                activeColor = MaterialTheme.colorScheme.secondary // Warna berbeda untuk kelembaban
            )
        }

        Spacer(modifier = Modifier.height(24.dp)) // Jarak antara gauge dan teks lainnya

        // Teks "Last Updated"
//        Text(
//            text = lastUpdated,
//            fontSize = 14.sp,
//            modifier = Modifier.padding(top = 16.dp)
//        )

        // Teks Pesan Error (jika ada)
//        errorMessage?.let {
//            Text(
//                text = it,
//                color = MaterialTheme.colorScheme.error,
//                fontSize = 14.sp,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//        }

        Spacer(modifier = Modifier.height(24.dp)) // Jarak sebelum tombol

        // Tombol "View History"
        Button(onClick = onViewHistoryClick) {
            Text("View History")
        }
    }
    // --- AKHIR DARI COLUMN UTAMA ---
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DhtScreenPreview() {
    DhtAppTheme {
        DhtScreen(viewModel = MainViewModel(), onViewHistoryClick = {})
    }
}