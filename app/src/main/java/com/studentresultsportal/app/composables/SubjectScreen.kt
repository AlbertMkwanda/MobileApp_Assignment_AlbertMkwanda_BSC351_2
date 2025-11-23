package com.studentresultsportal.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studentresultsportal.app.Subject

@Composable
fun SubjectScreen(subjects: List<Subject>, onSubjectClick: (Subject) -> Unit) {
    if (subjects.isEmpty()) {
        Text(text = "No subjects yet. Add one!", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(subjects) { subject ->
                SubjectItem(subject = subject, onClick = { onSubjectClick(subject) })
            }
        }
    }
}

@Composable
fun SubjectItem(subject: Subject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "${subject.name} (${subject.code})",
            modifier = Modifier.padding(16.dp)
        )
    }
}
