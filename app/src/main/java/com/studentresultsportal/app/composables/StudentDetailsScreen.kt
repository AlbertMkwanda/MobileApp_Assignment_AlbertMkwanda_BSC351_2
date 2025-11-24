package com.studentresultsportal.app.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studentresultsportal.app.Class
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Grade
import com.studentresultsportal.app.Subject
import com.studentresultsportal.app.calculateFinalGrade

@Composable
fun StudentDetailsScreen(studentId: Int, dao: DataAccessObject) {
    val studentClasses = dao.getClassesForStudent(studentId)
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(studentClasses) { studentClass ->
            StudentClassCard(studentClass = studentClass, studentId = studentId, dao = dao) {
                shareStudentResults(context, it)
            }
        }
    }
}

@Composable
fun StudentClassCard(
    studentClass: Class, 
    studentId: Int, 
    dao: DataAccessObject, 
    onLongPress: (String) -> Unit
) {
    val subjects = dao.getSubjectsForStudentInClass(studentId, studentClass.id)
    val grades = subjects.map { dao.getGradeForStudent(studentId, it.id, studentClass.id) }
    val overallGpa = calculateOverallGpa(grades.filterNotNull())
    val student = dao.getStudentById(studentId)

    val shareText = remember(student, studentClass, subjects, grades) {
        buildString {
            append("Student: ${student?.name} (${student?.regNumber})\n")
            append("Class: ${studentClass.name}\n")
            append("--------------------\n")
            subjects.forEach { subject ->
                val grade = grades.find { it?.subjectId == subject.id }
                val finalGrade = grade?.let { calculateFinalGrade(it) }
                append("${subject.name}: ${finalGrade?.let { "%.2f".format(it) } ?: "-"}\n")
            }
            append("\nOverall GPA: ${"%.2f".format(overallGpa)}\n")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(shareText) }
                )
            },
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

private fun shareStudentResults(context: Context, shareText: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
        setPackage("com.whatsapp")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Student Results"))
}
