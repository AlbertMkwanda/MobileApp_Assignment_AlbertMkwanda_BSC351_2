package com.studentresultsportal.app.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.studentresultsportal.app.Class
import com.studentresultsportal.app.DataAccessObject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassScreen(classes: List<Class>, onClassClick: (Class) -> Unit, dao: DataAccessObject, onClassDeleted: () -> Unit) {
    var classToDelete by remember { mutableStateOf<Class?>(null) }

    LazyColumn {
        items(classes) { classItem ->
            ListItem(
                headlineContent = { Text(classItem.name) },
                modifier = Modifier.combinedClickable(
                    onClick = { onClassClick(classItem) },
                    onLongClick = { classToDelete = classItem }
                )
            )
        }
    }

    classToDelete?.let { classItem ->
        DeleteConfirmationDialog(
            itemType = "class",
            itemName = classItem.name,
            onConfirm = {
                dao.deleteClass(classItem.id)
                classToDelete = null
                onClassDeleted()
            },
            onDismiss = { classToDelete = null }
        )
    }
}
