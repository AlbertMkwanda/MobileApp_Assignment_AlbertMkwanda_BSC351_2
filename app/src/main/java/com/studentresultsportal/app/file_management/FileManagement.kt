package com.studentresultsportal.app.file_management

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.studentresultsportal.app.Class
import com.studentresultsportal.app.DataAccessObject
import com.studentresultsportal.app.Student
import com.studentresultsportal.app.calculateFinalGrade
import com.studentresultsportal.app.composables.calculateOverallGpa
import java.io.File
import java.io.FileOutputStream

fun createCsvReport(context: Context, dao: DataAccessObject, classId: Int): File {
    val students = dao.getStudentsForClass(classId)
    val subjects = dao.getSubjectsForClass(classId)
    val studentResultsFile = File(context.cacheDir, "student_results.csv")
    studentResultsFile.writer().use {
        it.append("Student ID,Student Name,Subject,Final Grade,GPA\n")
        for (student in students) {
            for (subject in subjects) {
                val grade = dao.getGradeForStudent(student.id, subject.id, classId)
                if (grade != null) {
                    val finalGrade = calculateFinalGrade(grade)
                    val gpa = calculateOverallGpa(listOf(grade))
                    it.append("${student.regNumber},${student.name},${subject.name},$finalGrade,$gpa\n")
                }
            }
        }
    }
    return studentResultsFile
}

fun createPdfReport(context: Context, dao: DataAccessObject, classId: Int): File {
    val students = dao.getStudentsForClass(classId)
    val subjects = dao.getSubjectsForClass(classId)
    val pdfFile = File(context.cacheDir, "student_results.pdf")

    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    val tableTop = 40f
    val tableLeft = 20f
    val tableRight = 822f
    val rowHeight = 20f
    var yPosition = tableTop

    // Table Header
    canvas.drawText("Student Name", tableLeft + 5, yPosition, paint)
    subjects.forEachIndexed { index, subject ->
        canvas.drawText(subject.name, tableLeft + 150 + (index * 70), yPosition, paint)
    }
    canvas.drawText("Overall GPA", tableLeft + 150 + (subjects.size * 70), yPosition, paint)
    yPosition += rowHeight

    // Table Rows
    for (student in students) {
        canvas.drawText(student.name, tableLeft + 5, yPosition, paint)
        val grades = subjects.mapNotNull { subject -> dao.getGradeForStudent(student.id, subject.id, classId) }
        subjects.forEachIndexed { index, subject ->
            val grade = grades.find { it.subjectId == subject.id }
            if (grade != null) {
                val finalGrade = calculateFinalGrade(grade)
                canvas.drawText(String.format("%.2f", finalGrade), tableLeft + 150 + (index * 70), yPosition, paint)
            }
        }
        val overallGpa = calculateOverallGpa(grades)
        canvas.drawText(String.format("%.2f", overallGpa), tableLeft + 150 + (subjects.size * 70), yPosition, paint)
        yPosition += rowHeight
    }

    document.finishPage(page)
    FileOutputStream(pdfFile).use { document.writeTo(it) }
    document.close()

    return pdfFile
}
