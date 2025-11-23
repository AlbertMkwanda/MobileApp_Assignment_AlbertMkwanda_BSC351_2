package com.studentresultsportal.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studentresultsportal.app.Grade
import com.studentresultsportal.app.Student
import com.studentresultsportal.app.calculateFinalGrade

@Composable
fun StudentScreen(
    students: List<Student>,
    grades: Map<Int, Grade?>,
    onStudentClick: (Student) -> Unit
) {
    if (students.isEmpty()) {
        Text(text = "No students yet. Add one!", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(students) { student ->
                StudentItem(student = student, grade = grades[student.id], onClick = { onStudentClick(student) })
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, grade: Grade?, onClick: () -> Unit) {
    val finalGrade = grade?.let { calculateFinalGrade(it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, fontWeight = FontWeight.Bold)
                Text(text = student.regNumber)
            }
            Column {
                Text(text = "Assign 1: ${grade?.assignment1Score ?: "-"}")
                Text(text = "Assign 2: ${grade?.assignment2Score ?: "-"}")
                Text(text = "Mid Sem: ${grade?.midSemScore ?: "-"}")
                Text(text = "Exam: ${grade?.examScore ?: "-"}")
                Text(text = "Final: ${finalGrade?.let { String.format("%.2f", it) } ?: "-"}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
