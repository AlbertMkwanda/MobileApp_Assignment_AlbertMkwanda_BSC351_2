package com.studentresultsportal.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.studentresultsportal.app.Student

@Composable
fun AllStudentsScreen(students: List<Student>, onStudentClick: (Student) -> Unit) {
    LazyColumn {
        items(students) { student ->
            ListItem(
                headlineContent = { Text(student.name) },
                supportingContent = { Text(student.regNumber) },
                modifier = Modifier.clickable { onStudentClick(student) }
            )
        }
    }
}
