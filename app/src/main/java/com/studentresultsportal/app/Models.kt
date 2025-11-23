package com.studentresultsportal.app

data class Class(val id: Int, val name: String)

data class Student(val id: Int, val regNumber: String, val name: String)

data class Subject(
    val id: Int,
    val code: String,
    val name: String
)

data class Grade(
    val id: Int,
    val studentId: Int,
    val subjectId: Int,
    val classId: Int,
    val assignment1Score: Float?,
    val assignment2Score: Float?,
    val midSemScore: Float?,
    val examScore: Float?
)

fun calculateFinalGrade(grade: Grade): Float {
    val assignment1 = grade.assignment1Score ?: 0f
    val assignment2 = grade.assignment2Score ?: 0f
    val midSem = grade.midSemScore ?: 0f
    val exam = grade.examScore ?: 0f

    // Assuming weights: Assignment 1 (10%), Assignment 2 (10%), Mid-Sem (30%), Exam (50%)
    return (assignment1 * 0.1f) + (assignment2 * 0.1f) + (midSem * 0.3f) + (exam * 0.5f)
}
