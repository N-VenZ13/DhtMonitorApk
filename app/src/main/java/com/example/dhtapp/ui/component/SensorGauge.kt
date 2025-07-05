package com.example.dhtapp.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dhtapp.ui.theme.DhtAppTheme

@Composable
fun SensorGauge(
    value: Float,
    minValue: Float,
    maxValue: Float,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary, // Warna utama gauge
    inactiveColor: Color = Color.LightGray, // Warna latar belakang gauge
    valueTextColor: Color = Color.Black // Warna teks nilai sensor
) {
    Column(
        modifier = modifier.wrapContentSize(), // Wrap content agar menyesuaikan ukuran
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gauge Visual
        Box(
            modifier = Modifier
                .size(150.dp) // Ukuran lingkaran gauge
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 15.dp.toPx() // Ketebalan garis gauge
                val radius = size.minDimension / 2 - strokeWidth / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Gambar latar belakang gauge (lingkaran abu-abu)
                drawArc(
                    color = inactiveColor,
                    startAngle = 135f, // Mulai dari 135 derajat (kiri bawah)
                    sweepAngle = 270f, // Lingkaran 270 derajat
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Hitung sudut untuk nilai saat ini
                val percentage = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
                val sweepAngle = percentage * 270f

                // Gambar gauge aktif (warna primary)
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(activeColor.copy(alpha = 0.7f), activeColor)
                    ),
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Teks nilai sensor di bawah gauge
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "$value $unit", // Menampilkan nilai numerik
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = valueTextColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SensorGaugePreview() {
    DhtAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SensorGauge(
                value = 25.5f,
                minValue = 0f,
                maxValue = 50f,
                unit = "°C",
                label = "Temperature"
            )
            SensorGauge(
                value = 65.2f,
                minValue = 0f,
                maxValue = 100f,
                unit = "%",
                label = "Humidity",
                activeColor = MaterialTheme.colorScheme.secondary // Contoh warna berbeda
            )
        }
    }
}