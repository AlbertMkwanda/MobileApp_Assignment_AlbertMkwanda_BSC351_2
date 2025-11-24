package com.studentresultsportal.app

import android.content.ContentValues
import android.content.Context
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_ASSIGNMENT1_SCORE
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_ASSIGNMENT2_SCORE
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_CLASS_ID
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_CLASS_NAME
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_EXAM_SCORE
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_ID
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_MID_SEM_SCORE
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_STUDENT_ID
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_STUDENT_NAME
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_STUDENT_REG_NO
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_SUBJECT_CODE
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_SUBJECT_ID
import com.studentresultsportal.app.DatabaseHelper.Companion.COLUMN_SUBJECT_NAME
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_CLASSES
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_CLASS_STUDENTS
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_CLASS_SUBJECTS
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_GRADES
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_STUDENTS
import com.studentresultsportal.app.DatabaseHelper.Companion.TABLE_SUBJECTS

class DataAccessObject(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Class functions
    fun addClass(className: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CLASS_NAME, className)
        }
        return db.insert(TABLE_CLASSES, null, values)
    }

    fun deleteClass(classId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_CLASSES, "$COLUMN_ID = ?", arrayOf(classId.toString()))
        db.delete(TABLE_CLASS_STUDENTS, "$COLUMN_CLASS_ID = ?", arrayOf(classId.toString()))
        db.delete(TABLE_GRADES, "$COLUMN_CLASS_ID = ?", arrayOf(classId.toString()))
    }

    fun getAllClasses(): List<Class> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_CLASSES, null, null, null, null, null, null)
        val classes = mutableListOf<Class>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_CLASS_NAME))
                classes.add(Class(id, name))
            }
        }
        return classes
    }

    // Student functions
    fun addStudent(regNumber: String, name: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_REG_NO, regNumber)
            put(COLUMN_STUDENT_NAME, name)
        }
        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun deleteStudent(studentId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_STUDENTS, "$COLUMN_ID = ?", arrayOf(studentId.toString()))
        db.delete(TABLE_CLASS_STUDENTS, "$COLUMN_STUDENT_ID = ?", arrayOf(studentId.toString()))
        db.delete(TABLE_GRADES, "$COLUMN_STUDENT_ID = ?", arrayOf(studentId.toString()))
    }

    fun getStudentById(studentId: Int): Student? {
        val db = dbHelper.readableDatabase
        db.query(
            TABLE_STUDENTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(studentId.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val regNo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_REG_NO))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME))
                Student(id, regNo, name)
            } else {
                null
            }
        }
    }

    fun getStudentByRegNumber(regNumber: String): Student? {
        val db = dbHelper.readableDatabase
        db.query(
            TABLE_STUDENTS,
            null,
            "$COLUMN_STUDENT_REG_NO = ?",
            arrayOf(regNumber),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME))
                Student(id, regNumber, name)
            } else {
                null
            }
        }
    }

    fun addStudentToClass(studentId: Int, classId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_ID, studentId)
            put(COLUMN_CLASS_ID, classId)
        }
        return db.insert(TABLE_CLASS_STUDENTS, null, values)
    }

    fun getStudentsForClass(classId: Int): List<Student> {
        val db = dbHelper.readableDatabase
        val query = "SELECT s.* FROM $TABLE_STUDENTS s INNER JOIN $TABLE_CLASS_STUDENTS cs ON s.$COLUMN_ID = cs.$COLUMN_STUDENT_ID WHERE cs.$COLUMN_CLASS_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(classId.toString()))
        val students = mutableListOf<Student>()
        cursor.use { 
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val regNo = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_REG_NO))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_NAME))
                students.add(Student(id, regNo, name))
            }
        }
        return students
    }

    fun getAllStudents(): List<Student> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_STUDENTS, null, null, null, null, null, null)
        val students = mutableListOf<Student>()
        cursor.use { 
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val regNo = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_REG_NO))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_STUDENT_NAME))
                students.add(Student(id, regNo, name))
            }
        }
        return students
    }

    fun getClassesForStudent(studentId: Int): List<Class> {
        val db = dbHelper.readableDatabase
        val query = "SELECT c.* FROM $TABLE_CLASSES c INNER JOIN $TABLE_CLASS_STUDENTS cs ON c.$COLUMN_ID = cs.$COLUMN_CLASS_ID WHERE cs.$COLUMN_STUDENT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(studentId.toString()))
        val classes = mutableListOf<Class>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_CLASS_NAME))
                classes.add(Class(id, name))
            }
        }
        return classes
    }

    // Subject functions
    fun addSubject(code: String, name: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBJECT_CODE, code)
            put(COLUMN_SUBJECT_NAME, name)
        }
        return db.insert(TABLE_SUBJECTS, null, values)
    }

    fun deleteSubject(subjectId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_SUBJECTS, "$COLUMN_ID = ?", arrayOf(subjectId.toString()))
        db.delete(TABLE_GRADES, "$COLUMN_SUBJECT_ID = ?", arrayOf(subjectId.toString()))
    }

    fun addSubjectToClass(subjectId: Int, classId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBJECT_ID, subjectId)
            put(COLUMN_CLASS_ID, classId)
        }
        return db.insert(TABLE_CLASS_SUBJECTS, null, values)
    }

    fun getSubjectsForClass(classId: Int): List<Subject> {
        val db = dbHelper.readableDatabase
        val query = "SELECT s.* FROM $TABLE_SUBJECTS s INNER JOIN $TABLE_CLASS_SUBJECTS cs ON s.$COLUMN_ID = cs.$COLUMN_SUBJECT_ID WHERE cs.$COLUMN_CLASS_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(classId.toString()))
        val subjects = mutableListOf<Subject>()
        cursor.use { 
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val code = it.getString(it.getColumnIndexOrThrow(COLUMN_SUBJECT_CODE))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME))
                subjects.add(Subject(id, code, name))
            }
        }
        return subjects
    }

    fun getSubjectsForStudentInClass(studentId: Int, classId: Int): List<Subject> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT s.* FROM $TABLE_SUBJECTS s
            INNER JOIN $TABLE_GRADES g ON s.$COLUMN_ID = g.$COLUMN_SUBJECT_ID
            WHERE g.$COLUMN_STUDENT_ID = ? AND g.$COLUMN_CLASS_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(studentId.toString(), classId.toString()))
        val subjects = mutableListOf<Subject>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val code = it.getString(it.getColumnIndexOrThrow(COLUMN_SUBJECT_CODE))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME))
                subjects.add(Subject(id, code, name))
            }
        }
        return subjects
    }

    // Grade functions
    fun addOrUpdateGrade(studentId: Int, subjectId: Int, classId: Int, assignment1Score: Float?, assignment2Score: Float?, midSemScore: Float?, examScore: Float?): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_ID, studentId)
            put(COLUMN_SUBJECT_ID, subjectId)
            put(COLUMN_CLASS_ID, classId)
            assignment1Score?.let { put(COLUMN_ASSIGNMENT1_SCORE, it) }
            assignment2Score?.let { put(COLUMN_ASSIGNMENT2_SCORE, it) }
            midSemScore?.let { put(COLUMN_MID_SEM_SCORE, it) }
            examScore?.let { put(COLUMN_EXAM_SCORE, it) }
        }

        // Check if a grade already exists
        db.query(
            TABLE_GRADES,
            arrayOf(COLUMN_ID),
            "$COLUMN_STUDENT_ID = ? AND $COLUMN_SUBJECT_ID = ? AND $COLUMN_CLASS_ID = ?",
            arrayOf(studentId.toString(), subjectId.toString(), classId.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                val gradeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                db.update(TABLE_GRADES, values, "$COLUMN_ID = ?", arrayOf(gradeId.toString())).toLong()
            } else {
                db.insert(TABLE_GRADES, null, values)
            }
        }
    }

    fun getGradeForStudent(studentId: Int, subjectId: Int, classId: Int): Grade? {
        val db = dbHelper.readableDatabase
        db.query(
            TABLE_GRADES,
            null,
            "$COLUMN_STUDENT_ID = ? AND $COLUMN_SUBJECT_ID = ? AND $COLUMN_CLASS_ID = ?",
            arrayOf(studentId.toString(), subjectId.toString(), classId.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val assignment1Index = cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT1_SCORE)
                val assignment2Index = cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT2_SCORE)
                val midSemIndex = cursor.getColumnIndexOrThrow(COLUMN_MID_SEM_SCORE)
                val examIndex = cursor.getColumnIndexOrThrow(COLUMN_EXAM_SCORE)

                val assignment1Score = if (cursor.isNull(assignment1Index)) null else cursor.getFloat(assignment1Index)
                val assignment2Score = if (cursor.isNull(assignment2Index)) null else cursor.getFloat(assignment2Index)
                val midSemScore = if (cursor.isNull(midSemIndex)) null else cursor.getFloat(midSemIndex)
                val examScore = if (cursor.isNull(examIndex)) null else cursor.getFloat(examIndex)

                Grade(id, studentId, subjectId, classId, assignment1Score, assignment2Score, midSemScore, examScore)
            } else {
                null
            }
        }
    }

    fun getGradesForSubject(subjectId: Int): List<Grade> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_GRADES, null, "$COLUMN_SUBJECT_ID = ?", arrayOf(subjectId.toString()), null, null, null)
        val grades = mutableListOf<Grade>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val studentId = it.getInt(it.getColumnIndexOrThrow(COLUMN_STUDENT_ID))
                val classId = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLASS_ID))

                val assignment1Index = it.getColumnIndexOrThrow(COLUMN_ASSIGNMENT1_SCORE)
                val assignment2Index = it.getColumnIndexOrThrow(COLUMN_ASSIGNMENT2_SCORE)
                val midSemIndex = it.getColumnIndexOrThrow(COLUMN_MID_SEM_SCORE)
                val examIndex = it.getColumnIndexOrThrow(COLUMN_EXAM_SCORE)

                val assignment1Score = if (it.isNull(assignment1Index)) null else it.getFloat(assignment1Index)
                val assignment2Score = if (it.isNull(assignment2Index)) null else it.getFloat(assignment2Index)
                val midSemScore = if (it.isNull(midSemIndex)) null else it.getFloat(midSemIndex)
                val examScore = if (it.isNull(examIndex)) null else it.getFloat(examIndex)

                grades.add(Grade(id, studentId, subjectId, classId, assignment1Score, assignment2Score, midSemScore, examScore))
            }
        }
        return grades
    }

    fun getAllGrades(): List<Grade> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_GRADES, null, null, null, null, null, null)
        val grades = mutableListOf<Grade>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val studentId = it.getInt(it.getColumnIndexOrThrow(COLUMN_STUDENT_ID))
                val subjectId = it.getInt(it.getColumnIndexOrThrow(COLUMN_SUBJECT_ID))
                val classId = it.getInt(it.getColumnIndexOrThrow(COLUMN_CLASS_ID))
                
                val assignment1Index = it.getColumnIndexOrThrow(COLUMN_ASSIGNMENT1_SCORE)
                val assignment2Index = it.getColumnIndexOrThrow(COLUMN_ASSIGNMENT2_SCORE)
                val midSemIndex = it.getColumnIndexOrThrow(COLUMN_MID_SEM_SCORE)
                val examIndex = it.getColumnIndexOrThrow(COLUMN_EXAM_SCORE)

                val assignment1Score = if (it.isNull(assignment1Index)) null else it.getFloat(assignment1Index)
                val assignment2Score = if (it.isNull(assignment2Index)) null else it.getFloat(assignment2Index)
                val midSemScore = if (it.isNull(midSemIndex)) null else it.getFloat(midSemIndex)
                val examScore = if (it.isNull(examIndex)) null else it.getFloat(examIndex)

                grades.add(Grade(id, studentId, subjectId, classId, assignment1Score, assignment2Score, midSemScore, examScore))
            }
        }
        return grades
    }
}
