package com.studentresultsportal.app.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.calculateFinalGrade

@Composable
fun Dashboard() {
    val context = LocalContext.current
    val dao = remember { DataAccessObject(context) }
    val allGrades = remember { dao.getAllGrades() }

    val finalGrades = allGrades.mapNotNull { calculateFinalGrade(it) }

    // Create a frequency map of final grades for the chart
    val gradeDistribution = finalGrades
        .groupingBy { it.toInt() / 10 } // Group grades into buckets of 10
        .eachCount()

    val entries = gradeDistribution.entries.map { (gradeBucket, count) ->
        BarEntry(gradeBucket.toFloat() * 10, count.toFloat())
    }

    val dataSet = BarDataSet(entries, "Final Grade Distribution").apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
    }

    val barData = BarData(dataSet)

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = { ctx ->
            BarChart(ctx).apply {
                data = barData
                description.isEnabled = false
                setFitBars(true)
                invalidate()
            }
        }
    )
}
