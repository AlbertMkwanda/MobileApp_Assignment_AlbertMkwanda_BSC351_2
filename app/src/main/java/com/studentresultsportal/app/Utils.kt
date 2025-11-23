package com.studentresultsportal.app

fun calculateFinalGrade(grade: Grade?): Float? {
    if (grade == null) return null

    // Scores are out of: Assignment 1 (20), Assignment 2 (20), Mid-Sem (50) -> Total 90
    val courseworkTotal = (grade.assignment1Score ?: 0f) + (grade.assignment2Score ?: 0f) + (grade.midSemScore ?: 0f)

    // Normalize coursework to a percentage
    val courseworkPercentage = (courseworkTotal / 90f) * 100f

    // Calculate the weighted components
    val courseworkComponent = courseworkPercentage * 0.4f
    val examComponent = (grade.examScore ?: 0f) * 0.6f

    // Final grade is the sum of the weighted components
    val finalGrade = courseworkComponent + examComponent

    // Cap the final grade at 100
    return if (finalGrade > 100f) 100f else finalGrade
}
