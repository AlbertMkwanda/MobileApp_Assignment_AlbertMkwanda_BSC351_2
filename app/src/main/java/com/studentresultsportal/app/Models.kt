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

    // Continuous assessment is out of 90 (20 + 20 + 50)
    val continuousAssessmentScore = assignment1 + assignment2 + midSem
    // Final exam is out of 100
    val examScore = exam

    // Calculate 40% of the continuous assessment score (normalized to 90)
    val continuousAssessmentPart = (continuousAssessmentScore / 90f) * 40f
    // Calculate 60% of the final exam score (normalized to 100)
    val examPart = (examScore / 100f) * 60f

    return continuousAssessmentPart + examPart
}
