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
import com.studentresultsportal.app.Class

@Composable
fun ClassScreen(classes: List<Class>, onClassClick: (Class) -> Unit) {
    if (classes.isEmpty()) {
        Text(text = "No classes yet. Add one!", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(classes) { classItem ->
                ClassItem(classItem = classItem, onClick = { onClassClick(classItem) })
            }
        }
    }
}

@Composable
fun ClassItem(classItem: Class, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = classItem.name,
            modifier = Modifier.padding(16.dp)
        )
    }
}
