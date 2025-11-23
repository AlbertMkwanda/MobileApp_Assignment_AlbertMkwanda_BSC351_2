package com.studentresultsportal.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.studentresultsportal.app.composables.AboutScreen
import com.studentresultsportal.app.composables.ClassDashboard
import com.studentresultsportal.app.composables.ClassScreen
import com.studentresultsportal.app.composables.Dashboard
import com.studentresultsportal.app.composables.StudentScreen
import com.studentresultsportal.app.composables.SubjectScreen
import com.studentresultsportal.app.ui.theme.StudentResultsPortalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            StudentResultsPortalTheme(darkTheme = isDarkTheme) {
                GradebookApp(onToggleTheme = { isDarkTheme = !isDarkTheme })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradebookApp(onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dao = remember { DataAccessObject(context) }

    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showEditGradeDialog by remember { mutableStateOf<Student?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }

    var classes by remember { mutableStateOf(dao.getAllClasses()) }
    var subjects by remember { mutableStateOf(emptyList<Subject>()) }
    var students by remember { mutableStateOf(emptyList<Student>()) }
    var grades by remember { mutableStateOf(emptyMap<Int, Grade?>()) }

    var currentClassId by remember { mutableIntStateOf(0) }
    var currentSubjectId by remember { mutableIntStateOf(0) }

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search Students") }
                        )
                    } else {
                        val title = when (currentRoute) {
                            "dashboard" -> "Dashboard"
                            "classList" -> "Classes"
                            "about" -> "About"
                            "classDashboard/{classId}" -> "Class Dashboard"
                            "subjectList/{classId}" -> "Subjects"
                            "studentList/{subjectId}" -> "Students"
                            else -> "Lecturer's Gradebook"
                        }
                        Text(title)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                    if (currentRoute?.startsWith("studentList") == true) {
                        IconButton(onClick = { 
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) {
                                searchQuery = ""
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { showShareDialog = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Assessment, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Classes") },
                    label = { Text("Classes") },
                    selected = currentRoute == "classList",
                    onClick = { navController.navigate("classList") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About") },
                    selected = currentRoute == "about",
                    onClick = { navController.navigate("about") }
                )
            }
        },
        floatingActionButton = {
            when (currentRoute) {
                "classList" -> {
                    FloatingActionButton(onClick = { showAddClassDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Class")
                    }
                }
                "subjectList/{classId}" -> {
                    FloatingActionButton(onClick = { showAddSubjectDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Subject")
                    }
                }
                "studentList/{subjectId}" -> {
                    FloatingActionButton(onClick = { showAddStudentDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Student")
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "dashboard", modifier = Modifier.padding(innerPadding)) {
            composable("dashboard") {
                Dashboard()
            }
            composable("about") {
                AboutScreen()
            }
            composable("classList") {
                ClassScreen(classes = classes, onClassClick = { 
                    navController.navigate("classDashboard/${it.id}") 
                })
            }
            composable("classDashboard/{classId}") { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId")?.toIntOrNull() ?: 0
                currentClassId = classId
                ClassDashboard(classId = classId, onNavigateToSubjects = {
                    navController.navigate("subjectList/$it")
                })
            }
            composable("subjectList/{classId}") { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId")?.toIntOrNull() ?: 0
                currentClassId = classId
                subjects = dao.getSubjectsForClass(classId)
                SubjectScreen(subjects = subjects, onSubjectClick = { 
                    currentSubjectId = it.id
                    navController.navigate("studentList/${it.id}")
                })
            }
            composable("studentList/{subjectId}") { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId")?.toIntOrNull() ?: 0
                currentSubjectId = subjectId
                students = dao.getStudentsForClass(currentClassId)
                grades = students.associate { student -> student.id to dao.getGradeForStudent(student.id, currentSubjectId, currentClassId) }
                
                val filteredStudents = if (isSearchActive) {
                    students.filter { it.name.contains(searchQuery, ignoreCase = true) || it.regNumber.contains(searchQuery, ignoreCase = true) }
                } else {
                    students
                }
                StudentScreen(students = filteredStudents, grades = grades, onStudentClick = { showEditGradeDialog = it })
            }
        }

        if (showAddClassDialog) {
            AddClassDialog(
                onDismiss = { showAddClassDialog = false },
                onAdd = {
                    dao.addClass(it)
                    classes = dao.getAllClasses()
                    showAddClassDialog = false
                }
            )
        }

        if (showAddSubjectDialog) {
            AddSubjectDialog(
                onDismiss = { showAddSubjectDialog = false },
                onAdd = { code, name ->
                    val subjectId = dao.addSubject(code, name)
                    dao.addSubjectToClass(subjectId.toInt(), currentClassId)
                    subjects = dao.getSubjectsForClass(currentClassId)
                    showAddSubjectDialog = false
                }
            )
        }

        if (showAddStudentDialog) {
            AddStudentDialog(
                onDismiss = { showAddStudentDialog = false },
                onAdd = { regNo, name ->
                    val studentId = dao.addStudent(regNo, name).toInt()
                    dao.addStudentToClass(studentId, currentClassId)
                    dao.addOrUpdateGrade(studentId, currentSubjectId, currentClassId, null, null, null, null)
                    students = dao.getStudentsForClass(currentClassId)
                    grades = students.associate { student -> student.id to dao.getGradeForStudent(student.id, currentSubjectId, currentClassId) }
                    showAddStudentDialog = false
                }
            )
        }

        showEditGradeDialog?.let { student ->
            EditGradeDialog(
                student = student,
                grade = grades[student.id],
                onDismiss = { showEditGradeDialog = null },
                onSave = { assignment1, assignment2, midSem, exam ->
                    dao.addOrUpdateGrade(student.id, currentSubjectId, currentClassId, assignment1, assignment2, midSem, exam)
                    grades = students.associate { s -> s.id to dao.getGradeForStudent(s.id, currentSubjectId, currentClassId) }
                    showEditGradeDialog = null
                }
            )
        }

        if (showShareDialog) {
            ShareStudentDialog(
                students = students,
                onDismiss = { showShareDialog = false },
                onShare = { student ->
                    val grade = grades[student.id]
                    val subject = subjects.find { it.id == currentSubjectId }
                    val finalGrade = grade?.let { calculateFinalGrade(it) }

                    val shareText = """
                    Student: ${student.name} (${student.regNumber})
                    Subject: ${subject?.name ?: "N/A"}
                    --------------------
                    Assignment 1: ${grade?.assignment1Score ?: "-"}
                    Assignment 2: ${grade?.assignment2Score ?: "-"}
                    Mid-Semester: ${grade?.midSemScore ?: "-"}
                    Final Exam: ${grade?.examScore ?: "-"}
                    --------------------
                    Final Grade: ${finalGrade?.let { String.format("%.2f", it) } ?: "-"}
                    """.trimIndent()

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Student Results"))
                    showShareDialog = false
                }
            )
        }
    }
}

@Composable
fun ShareStudentDialog(students: List<Student>, onDismiss: () -> Unit, onShare: (Student) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Results for...") },
        text = {
            LazyColumn {
                items(students) { student ->
                    Text(
                        text = student.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShare(student) }
                            .padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { 
            Button(onClick = onDismiss) {
                Text("Cancel")
            } 
        }
    )
}

@Composable
fun AddClassDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var className by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class") },
        text = {
            Column {
                Text("Enter class name:")
                TextField(
                    value = className,
                    onValueChange = { className = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(className) }) {
                Text("Add")
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
fun AddSubjectDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subject") },
        text = {
            Column {
                TextField(value = code, onValueChange = { code = it }, placeholder = { Text("Subject Code") })
                TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Subject Name") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(code, name) }) {
                Text("Add")
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
fun AddStudentDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var regNo by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student") },
        text = {
            Column {
                TextField(value = regNo, onValueChange = { regNo = it }, placeholder = { Text("Registration Number") })
                TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Student Name") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(regNo, name) }) {
                Text("Add")
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
fun EditGradeDialog(student: Student, grade: Grade?, onDismiss: () -> Unit, onSave: (Float?, Float?, Float?, Float?) -> Unit) {
    var assignment1Score by remember { mutableStateOf(grade?.assignment1Score?.toString() ?: "") }
    var assignment2Score by remember { mutableStateOf(grade?.assignment2Score?.toString() ?: "") }
    var midSemScore by remember { mutableStateOf(grade?.midSemScore?.toString() ?: "") }
    var examScore by remember { mutableStateOf(grade?.examScore?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Grade for ${student.name}") },
        text = {
            Column {
                TextField(value = assignment1Score, onValueChange = { assignment1Score = it }, placeholder = { Text("Assignment 1 (20)") })
                TextField(value = assignment2Score, onValueChange = { assignment2Score = it }, placeholder = { Text("Assignment 2 (20)") })
                TextField(value = midSemScore, onValueChange = { midSemScore = it }, placeholder = { Text("Mid-Semester (50)") })
                TextField(value = examScore, onValueChange = { examScore = it }, placeholder = { Text("Exam (100)") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(assignment1Score.toFloatOrNull(), assignment2Score.toFloatOrNull(), midSemScore.toFloatOrNull(), examScore.toFloatOrNull()) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    var isDarkTheme by remember { mutableStateOf(false) }
    StudentResultsPortalTheme(darkTheme = isDarkTheme) {
        GradebookApp(onToggleTheme = { isDarkTheme = !isDarkTheme })
    }
}
