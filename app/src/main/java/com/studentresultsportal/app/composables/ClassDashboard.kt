package com.studentresultsportal.app.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.studentresultsportal.app.BuildConfig
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Student
import com.studentresultsportal.app.Subject
import com.studentresultsportal.app.file_management.createCsvReport
import com.studentresultsportal.app.file_management.createPdfReport

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassDashboard(
    classId: Int,
    dao: DataAccessObject,
    onAddSubjectClick: () -> Unit,
    onSubjectDeleted: () -> Unit,
    onSubjectClick: (Subject) -> Unit,
    onStudentClick: (Student) -> Unit
) {
    var showEnrollStudentDialog by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Students", "Subjects")

    val students = remember(classId, showEnrollStudentDialog) { dao.getStudentsForClass(classId) }
    val subjects = remember(classId, onSubjectDeleted) { dao.getSubjectsForClass(classId) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { onAddSubjectClick() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Subject")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { showEnrollStudentDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Enroll Student")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Enroll")
            }
            Box {
                IconButton(onClick = { showExportMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Export Options")
                }
                DropdownMenu(
                    expanded = showExportMenu,
                    onDismissRequest = { showExportMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Export as CSV") },
                        onClick = { 
                            showExportMenu = false
                            val csvFile = createCsvReport(context, dao, classId)
                            shareFile(context, csvFile, "text/csv")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Export as PDF") },
                        onClick = { 
                            showExportMenu = false
                            val pdfFile = createPdfReport(context, dao, classId)
                            shareFile(context, pdfFile, "application/pdf")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        when (tabIndex) {
            0 -> {
                Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                        items(students) { student ->
                            ListItem(
                                headlineContent = { Text(student.name) },
                                supportingContent = { Text(student.regNumber) },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Student"
                                    )
                                },
                                modifier = Modifier.clickable { onStudentClick(student) }
                            )
                        }
                    }
                }
            }
            1 -> {
                Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                        items(subjects) { subject ->
                            ListItem(
                                headlineContent = { Text(subject.name) },
                                leadingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Article,
                                        contentDescription = "Subject"
                                    )
                                },
                                modifier = Modifier.combinedClickable(
                                    onClick = { onSubjectClick(subject) },
                                    onLongClick = { subjectToDelete = subject }
                                )
                            )
                        }
                    }
                }
            }
        }

        if (showEnrollStudentDialog) {
            EnrollStudentDialog(
                classId = classId,
                dao = dao,
                onDismiss = { showEnrollStudentDialog = false },
                onStudentEnrolled = { showEnrollStudentDialog = false }
            )
        }

        subjectToDelete?.let { subject ->
            DeleteConfirmationDialog(
                itemType = "subject",
                itemName = subject.name,
                onConfirm = {
                    dao.deleteSubject(subject.id)
                    subjectToDelete = null
                    onSubjectDeleted()
                },
                onDismiss = { subjectToDelete = null }
            )
        }
    }
}

@Composable
private fun EnrollStudentDialog(
    classId: Int,
    dao: DataAccessObject,
    onDismiss: () -> Unit,
    onStudentEnrolled: () -> Unit
) {
    val allStudents = remember { dao.getAllStudents() }
    val enrolledStudents = remember { dao.getStudentsForClass(classId) }
    val unenrolledStudents = remember(allStudents, enrolledStudents) {
        allStudents.filter { student -> enrolledStudents.none { it.id == student.id } }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    val filteredStudents = if (searchQuery.isBlank()) {
        unenrolledStudents
    } else {
        unenrolledStudents.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enroll Student") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search for students...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (filteredStudents.isEmpty()) {
                    Text("No students found.", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn {
                        items(filteredStudents) { student ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedStudent = student }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedStudent?.id == student.id,
                                    onClick = { selectedStudent = student }
                                )
                                Text(text = "${student.name} (${student.regNumber})")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let {
                        dao.addStudentToClass(it.id, classId)
                        onStudentEnrolled()
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

@Composable
fun DeleteConfirmationDialog(
    itemType: String,
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete $itemType") },
        text = { Text("Are you sure you want to delete $itemName?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun shareFile(context: Context, file: java.io.File, mimeType: String) {
    val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = mimeType
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Export Results"))
}
