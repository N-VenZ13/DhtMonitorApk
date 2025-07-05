package com.example.dhtapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dhtapp.R
import com.example.dhtapp.data.DhtData
import com.example.dhtapp.ui.theme.DhtAppTheme


import androidx.compose.runtime.LaunchedEffect // Tambahkan import ini
import androidx.compose.ui.platform.LocalLifecycleOwner // Tambahkan import ini
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import android.util.Log // Tambahkan import ini


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
    onBackClick: () -> Unit // Callback untuk tombol kembali
) {
    val historyData by viewModel.historyData.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // Ketika HistoryScreen berada dalam kondisi STARTED (terlihat oleh user)
            // panggil startFetchingDataPeriodically.
            // Coroutine ini akan dibatalkan secara otomatis saat HistoryScreen keluar dari STARTED.
            Log.d("HistoryScreen", "HistoryScreen is STARTED. Starting periodic fetch.")
            viewModel.startFetchingDataPeriodically()

            // Catatan: onDispose dari LaunchedEffect akan dipanggil saat Composable ini keluar dari komposisi.
            // Namun, karena kita menggunakan repeatOnLifecycle(STARTED),
            // startFetchingDataPeriodically akan dipanggil saat memasuki STARTED,
            // dan akan di-cancel oleh stopFetchingDataPeriodically jika state berubah dari STARTED.
            // Tidak perlu onDispose explisit di LaunchedEffect ini untuk timer,
            // karena ViewModel sudah menanganinya di onCleared() dan setiap kali startFetching dipanggil.
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensor History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // Icon panah kembali
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) // Progress bar
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (historyData.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "No historical data to display.",
                    modifier = Modifier.padding(top = 32.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyData) { data ->
                        HistoryItem(data = data)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(data: DhtData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Suhu: ${String.format("%.1f", data.temperature)} °C",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kelembaban: ${String.format("%.1f", data.humidity)} %",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Recorded At: ${data.createdAt.substring(0, 19).replace("T", " ")}",
//                fontSize = 12.sp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    DhtAppTheme {
        // Dummy data for preview
        val dummyData = listOf(
            DhtData(1, 25.0f, 60.0f, "2023-10-26T10:00:00Z", ""),
            DhtData(2, 25.1f, 60.1f, "2023-10-26T10:05:00Z", ""),
            DhtData(3, 25.2f, 60.2f, "2023-10-26T10:10:00Z", "")
        )
        // Kita perlu provide ViewModel secara manual di preview atau mock
        // Untuk tujuan preview, bisa juga langsung tampilkan komponen tanpa ViewModel
        // Atau buat ViewModel dengan data dummy seperti ini:
//        val viewModel = HistoryViewModel()
//        viewModel.historyData.value = dummyData

        HistoryScreen(onBackClick = {})
    }
}