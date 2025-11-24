# Lecturer's Gradebook

Lecturer's Gradebook is a modern Android application designed for lecturers to manage student results efficiently. Built with Jetpack Compose, this app provides a clean, intuitive, and modern user interface for managing classes, students, subjects, and grades.

## Features

- **Class Management:** Create and manage classes, each with its own set of subjects and enrolled students.
- **Student Management:** Add, view, and delete students from the gradebook.
- **Subject Management:** Add and remove subjects from a class.
- **Grade Entry:** Easily enter and update student grades for each subject, including assignments and exams.
- **GPA Calculation:** The app automatically calculates the final grade and GPA for each student on a 5.0 scale.
- **Data Import/Export:**
    - **Import Students from CSV:** Quickly populate your student list by importing a CSV file.
    - **Export Results to CSV/PDF:** Export a complete report of a class's results in either CSV or PDF format.
- **Share Student Results:** Share a student's complete results for a specific class via WhatsApp or other messaging apps.
- **Modern UI:** A clean and professional UI built with Material 3 and Jetpack Compose, including a dark theme.

## Getting Started

### CSV Import Format

To import students from a CSV file, the file must be formatted as follows:

```csv
REG_NUMBER,STUDENT_NAME
SCT211-0001/2021,John Doe
SCT211-0002/2021,Jane Smith
```

- The first column must be the student's registration number.
- The second column must be the student's full name.

### How to Use the App

1.  **Add a Class:** From the "Classes" screen, tap the "+" button to add a new class.
2.  **Add Subjects:** In the "Class Dashboard," tap "Add Subject" to add subjects to the class.
3.  **Enroll Students:**
    - You can enroll students individually by tapping "Enroll Student" in the "Class Dashboard."
    - Alternatively, you can import a list of students from a CSV file from the "All Students" screen.
4.  **Enter Grades:**
    - Navigate to the "Class Dashboard" and select a subject.
    - Tap on a student to open the grade entry dialog and enter their scores.
5.  **Export Results:**
    - From the "Class Dashboard," tap the ellipsis (three dots) menu and select "Export as CSV" or "Export as PDF."
6.  **Share Student Results:**
    - Go to the "Students" screen and select a student.
    - Long-press on the class card for which you want to share the results. This will open a share dialog to send the results via WhatsApp.

## Built With

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3](https://m3.material.io/)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
