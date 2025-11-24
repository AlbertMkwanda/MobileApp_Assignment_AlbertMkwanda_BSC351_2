package com.studentresultsportal.app.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studentresultsportal.app.Class
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Grade
import com.studentresultsportal.app.Subject
import com.studentresultsportal.app.calculateFinalGrade

@Composable
fun StudentDetailsScreen(studentId: Int, dao: DataAccessObject) {
    val studentClasses = dao.getClassesForStudent(studentId)

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(studentClasses) { studentClass ->
            StudentClassCard(studentClass = studentClass, studentId = studentId, dao = dao)
        }
    }
}

@Composable
fun StudentClassCard(studentClass: Class, studentId: Int, dao: DataAccessObject) {
    val subjects = dao.getSubjectsForStudentInClass(studentId, studentClass.id)
    val grades = subjects.map { dao.getGradeForStudent(studentId, it.id, studentClass.id) }
    val overallGpa = calculateOverallGpa(grades.filterNotNull())

    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Class: ${studentClass.name}", style = MaterialTheme.typography.titleMedium)
            Text("Overall GPA: ${String.format("%.2f", overallGpa)}", style = MaterialTheme.typography.titleSmall)
            subjects.forEach { subject ->
                val grade = dao.getGradeForStudent(studentId, subject.id, studentClass.id)
                if (grade != null) {
                    SubjectGradeItem(subject = subject, grade = grade)
                }
            }
        }
    }
}

@Composable
fun SubjectGradeItem(subject: Subject, grade: Grade) {
    val finalGrade = calculateFinalGrade(grade)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("${subject.name}: ${String.format("%.2f", finalGrade)}")
    }
}

fun calculateOverallGpa(grades: List<Grade>): Float {
    if (grades.isEmpty()) return 0.0f
    val totalPoints = grades.sumOf { gradeToGpa(calculateFinalGrade(it)).toDouble() }
    return (totalPoints / grades.size).toFloat()
}

fun gradeToGpa(grade: Float): Float {
    return when {
        grade >= 80 -> 5.0f
        grade >= 70 -> 4.0f
        grade >= 60 -> 3.0f
        grade >= 50 -> 2.0f
        grade >= 40 -> 1.0f
        else -> 0.0f
    }
}
