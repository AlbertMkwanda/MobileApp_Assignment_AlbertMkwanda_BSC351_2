package com.studentresultsportal.app.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun ClassDashboard(classId: Int, onNavigateToSubjects: (Int) -> Unit) {
    val context = LocalContext.current
    val dao = remember { DataAccessObject(context) }
    val allGrades = remember { dao.getAllGrades() }

    val classGrades = allGrades.filter { it.classId == classId }
    val finalGrades = classGrades.mapNotNull { calculateFinalGrade(it) }

    // Create a frequency map of final grades for the chart
    val gradeDistribution = finalGrades
        .groupingBy { (it.toInt() / 10) * 10 } // Group grades into buckets of 10 (e.g., 70s, 80s)
        .eachCount()

    val entries = gradeDistribution.entries.map { (gradeBucket, count) ->
        BarEntry(gradeBucket.toFloat(), count.toFloat())
    }

    val dataSet = BarDataSet(entries, "Final Grade Distribution").apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
    }

    val barData = BarData(dataSet)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->
                BarChart(ctx).apply {
                    data = barData
                    description.isEnabled = false
                    setFitBars(true)
                    invalidate()
                }
            }
        )
        Button(onClick = { onNavigateToSubjects(classId) }) {
            Text("View Subjects")
        }
    }
}
