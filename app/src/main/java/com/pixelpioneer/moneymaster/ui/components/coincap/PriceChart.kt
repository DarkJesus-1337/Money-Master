package com.pixelpioneer.moneymaster.ui.components.coincap

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PriceChart(
    historyData: List<HistoryDataPoint>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val materialTextColor = MaterialTheme.colorScheme.onSecondaryContainer.toArgb()

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = true
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                legend.isEnabled = false

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = materialTextColor
                    textSize = 10f
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return dateFormat.format(Date(value.toLong()))
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    textColor = materialTextColor
                    textSize = 10f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return FormatUtils.formatCurrency(value.toDouble())
                        }
                    }
                }

                axisRight.isEnabled = false
            }
        },
        modifier = modifier,
        update = { chart ->
            if (historyData.isNotEmpty()) {
                val entries = historyData.mapIndexed { index, dataPoint ->
                    Entry(
                        dataPoint.time.toFloat(),
                        dataPoint.priceUsd.toFloatOrNull() ?: 0f
                    )
                }

                val dataSet = LineDataSet(entries, "Price").apply {
                    color = Color(0xFFA921F3).toArgb()
                    setCircleColor(Color(color = 0xFFEEA73D).toArgb())
                    lineWidth = 2f
                    circleRadius = 3f
                    setDrawCircleHole(false)
                    valueTextSize = 10f
                    setDrawValues(true)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = Color(0xB5673AB7).copy(alpha = 0.3f).toArgb()
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        }
    )
}

@Preview(
    showBackground = true,
    )
@Composable
private fun PriceChartPreview() {
    val sampleHistoryData = listOf(
        HistoryDataPoint(
            priceUsd = "50000.123",
            time = System.currentTimeMillis() - 24 * 60 * 60 * 1000, // vor 24h
            date = ""
        ),
        HistoryDataPoint(
            priceUsd = "51500.456",
            time = System.currentTimeMillis() - 18 * 60 * 60 * 1000, // vor 18h
            date = ""
        ),
        HistoryDataPoint(
            priceUsd = "49800.789",
            time = System.currentTimeMillis() - 12 * 60 * 60 * 1000, // vor 12h
            date = ""
        ),
        HistoryDataPoint(
            priceUsd = "52200.012",
            time = System.currentTimeMillis() - 6 * 60 * 60 * 1000, // vor 6h
            date = ""
        ),
        HistoryDataPoint(
            priceUsd = "53100.345",
            time = System.currentTimeMillis(), // jetzt
            date = ""
        )
    )

    PriceChart(
        historyData = sampleHistoryData,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

