package com.studentresultsportal.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Student

@Composable
fun EnrollStudentDialog(
    classId: Int,
    dao: DataAccessObject,
    onDismiss: () -> Unit
) {
    val allStudents = dao.getAllStudents()
    val enrolledStudents = dao.getStudentsForClass(classId)
    val unenrolledStudents = allStudents.filter { it !in enrolledStudents }

    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enroll Student") },
        text = {
            LazyColumn {
                items(unenrolledStudents) { student ->
                    Text(
                        text = student.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStudent = student }
                            .padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let { 
                        dao.addStudentToClass(it.id, classId)
                        onDismiss()
                    }
                },
                enabled = selectedStudent != null
            ) {
                Text("Enroll")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
