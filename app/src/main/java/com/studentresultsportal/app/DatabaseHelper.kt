package com.studentresultsportal.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Gradebook.db"
        private const val DATABASE_VERSION = 4 // Incremented version

        // Table Names
        const val TABLE_CLASSES = "classes"
        const val TABLE_STUDENTS = "students"
        const val TABLE_SUBJECTS = "subjects"
        const val TABLE_GRADES = "grades"
        const val TABLE_CLASS_SUBJECTS = "class_subjects"
        const val TABLE_CLASS_STUDENTS = "class_students"

        // Common column names
        const val COLUMN_ID = "id"

        // CLASSES Table - column names
        const val COLUMN_CLASS_NAME = "name"

        // STUDENTS Table - column names
        const val COLUMN_STUDENT_REG_NO = "reg_number"
        const val COLUMN_STUDENT_NAME = "name"

        // SUBJECTS Table - column names
        const val COLUMN_SUBJECT_CODE = "code"
        const val COLUMN_SUBJECT_NAME = "name"

        // GRADES Table - column names
        const val COLUMN_STUDENT_ID = "student_id"
        const val COLUMN_SUBJECT_ID = "subject_id"
        const val COLUMN_CLASS_ID = "class_id"
        const val COLUMN_ASSIGNMENT1_SCORE = "assignment1_score"
        const val COLUMN_ASSIGNMENT2_SCORE = "assignment2_score"
        const val COLUMN_MID_SEM_SCORE = "mid_sem_score"
        const val COLUMN_EXAM_SCORE = "exam_score"

        // CLASS_SUBJECTS Table - column names
        // COLUMN_CLASS_ID is already defined
        // COLUMN_SUBJECT_ID is already defined

        // CLASS_STUDENTS Table - column names
        // COLUMN_CLASS_ID is already defined
        // COLUMN_STUDENT_ID is already defined
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createClassesTable = """CREATE TABLE $TABLE_CLASSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLASS_NAME TEXT NOT NULL)"""

        val createStudentsTable = """CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STUDENT_REG_NO TEXT NOT NULL UNIQUE,
                $COLUMN_STUDENT_NAME TEXT NOT NULL)"""

        val createSubjectsTable = """CREATE TABLE $TABLE_SUBJECTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBJECT_CODE TEXT NOT NULL UNIQUE,
                $COLUMN_SUBJECT_NAME TEXT NOT NULL)"""

        val createClassSubjectsTable = """CREATE TABLE $TABLE_CLASS_SUBJECTS (
                $COLUMN_CLASS_ID INTEGER NOT NULL,
                $COLUMN_SUBJECT_ID INTEGER NOT NULL,
                PRIMARY KEY ($COLUMN_CLASS_ID, $COLUMN_SUBJECT_ID),
                FOREIGN KEY($COLUMN_CLASS_ID) REFERENCES $TABLE_CLASSES($COLUMN_ID),
                FOREIGN KEY($COLUMN_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_ID))"""

        val createClassStudentsTable = """CREATE TABLE $TABLE_CLASS_STUDENTS (
                $COLUMN_CLASS_ID INTEGER NOT NULL,
                $COLUMN_STUDENT_ID INTEGER NOT NULL,
                PRIMARY KEY ($COLUMN_CLASS_ID, $COLUMN_STUDENT_ID),
                FOREIGN KEY($COLUMN_CLASS_ID) REFERENCES $TABLE_CLASSES($COLUMN_ID),
                FOREIGN KEY($COLUMN_STUDENT_ID) REFERENCES $TABLE_STUDENTS($COLUMN_ID))"""

        val createGradesTable = """CREATE TABLE $TABLE_GRADES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STUDENT_ID INTEGER NOT NULL,
                $COLUMN_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_CLASS_ID INTEGER NOT NULL,
                $COLUMN_ASSIGNMENT1_SCORE REAL,
                $COLUMN_ASSIGNMENT2_SCORE REAL,
                $COLUMN_MID_SEM_SCORE REAL,
                $COLUMN_EXAM_SCORE REAL,
                FOREIGN KEY($COLUMN_STUDENT_ID) REFERENCES $TABLE_STUDENTS($COLUMN_ID),
                FOREIGN KEY($COLUMN_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_ID),
                FOREIGN KEY($COLUMN_CLASS_ID) REFERENCES $TABLE_CLASSES($COLUMN_ID))"""

        db.execSQL(createClassesTable)
        db.execSQL(createStudentsTable)
        db.execSQL(createSubjectsTable)
        db.execSQL(createClassSubjectsTable)
        db.execSQL(createClassStudentsTable)
        db.execSQL(createGradesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GRADES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS_SUBJECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS_STUDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
        onCreate(db)
    }
}
