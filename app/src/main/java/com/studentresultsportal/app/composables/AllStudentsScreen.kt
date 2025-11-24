package com.studentresultsportal.app.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.studentresultsportal.app.BuildConfig
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Student
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AllStudentsScreen(
    students: List<Student>,
    onStudentClick: (Student) -> Unit,
    dao: DataAccessObject,
    onStudentsImported: () -> Unit,
    onStudentDeleted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    val filteredStudents = if (searchQuery.isBlank()) {
        students
    } else {
        students.filter { it.name.contains(searchQuery, ignoreCase = true) || it.regNumber.contains(searchQuery, ignoreCase = true) }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { 
            scope.launch {
                importStudentsFromCsv(context, it, dao)
                onStudentsImported()
            }
        }
    }

    Column {
        TopAppBar(
            title = {
                if (isSearchActive) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search students...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("All Students")
                }
            },
            actions = {
                if (isSearchActive) {
                    IconButton(onClick = { 
                        isSearchActive = false
                        searchQuery = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Search")
                    }
                } else {
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Students")
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Import from CSV") },
                            onClick = { 
                                showMenu = false
                                importLauncher.launch("*/*")
                            }
                        )
                    }
                }
            }
        )
        LazyColumn {
            items(filteredStudents) { student ->
                ListItem(
                    headlineContent = { Text(student.name) },
                    supportingContent = { Text(student.regNumber) },
                    modifier = Modifier.combinedClickable(
                        onClick = { onStudentClick(student) },
                        onLongClick = { studentToDelete = student }
                    )
                )
            }
        }
    }

    studentToDelete?.let { student ->
        DeleteConfirmationDialog(
            itemType = "student",
            itemName = student.name,
            onConfirm = {
                dao.deleteStudent(student.id)
                studentToDelete = null
                onStudentDeleted()
            },
            onDismiss = { studentToDelete = null }
        )
    }
}

private fun importStudentsFromCsv(context: Context, uri: Uri, dao: DataAccessObject) {
    context.contentResolver.openInputStream(uri)?.use {
        BufferedReader(InputStreamReader(it)).forEachLine { line ->
            val tokens = line.split(",")
            if (tokens.size == 2) {
                val regNumber = tokens[0]
                val name = tokens[1]
                if (dao.getStudentByRegNumber(regNumber) == null) {
                    dao.addStudent(regNumber, name)
                }
            }
        }
    }
}
