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

fun createCsvReport(context: Context, dao: DataAccessObject): File {
    val students = dao.getAllStudents()
    val classes = dao.getAllClasses()
    val csvFile = File(context.cacheDir, "student_results.csv")
    csvFile.writer().use {
        it.append("Student ID,Student Name,Class,Subject,Final Grade,GPA\n")
        for (student in students) {
            for (studentClass in classes) {
                val subjects = dao.getSubjectsForStudentInClass(student.id, studentClass.id)
                val grades = subjects.mapNotNull { subject -> dao.getGradeForStudent(student.id, subject.id, studentClass.id) }
                for (grade in grades) {
                    val subject = subjects.find { it.id == grade.subjectId }
                    val finalGrade = calculateFinalGrade(grade)
                    val gpa = calculateOverallGpa(listOf(grade))
                    it.append("${student.regNumber},${student.name},${studentClass.name},${subject?.name ?: ""},$finalGrade,$gpa\n")
                }
            }
        }
    }
    return csvFile
}

fun createPdfReport(context: Context, dao: DataAccessObject): File {
    val students = dao.getAllStudents()
    val classes = dao.getAllClasses()
    val pdfFile = File(context.cacheDir, "student_results.pdf")

    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    var yPosition = 20f
    for (student in students) {
        canvas.drawText("Student: ${student.name} (${student.regNumber})", 10f, yPosition, paint)
        yPosition += 20f

        for (studentClass in dao.getClassesForStudent(student.id)) {
            canvas.drawText("  Class: ${studentClass.name}", 10f, yPosition, paint)
            yPosition += 20f

            val subjects = dao.getSubjectsForStudentInClass(student.id, studentClass.id)
            val grades = subjects.mapNotNull { subject -> dao.getGradeForStudent(student.id, subject.id, studentClass.id) }
            val overallGpa = calculateOverallGpa(grades)

            canvas.drawText("    Overall GPA: ${String.format("%.2f", overallGpa)}", 10f, yPosition, paint)
            yPosition += 20f

            for (grade in grades) {
                val subject = subjects.find { it.id == grade.subjectId }
                val finalGrade = calculateFinalGrade(grade)
                canvas.drawText("      ${subject?.name ?: ""}: ${String.format("%.2f", finalGrade)}", 10f, yPosition, paint)
                yPosition += 20f
            }
        }
        yPosition += 20f
    }

    document.finishPage(page)
    FileOutputStream(pdfFile).use { document.writeTo(it) }
    document.close()

    return pdfFile
}
