# MobileApp_Assignment_AlbertMkwanda_BSC351_2

# Lecturer's Gradebook

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=for-the-badge&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=for-the-badge&logo=android)
![Database](https://img.shields.io/badge/Database-SQLite-blue.svg?style=for-the-badge&logo=sqlite)

A standalone, offline-first Android application designed for lecturers to efficiently manage student grades, class lists, and subject details directly on their mobile device. This app replaces traditional spreadsheets and paper-based systems with a secure, dedicated, and easy-to-use digital gradebook.

## Core Features

The application is designed around the core needs of a lecturer for robust and accessible record-keeping.

*   **Offline First:** All data is stored locally in an SQLite database. The app is fully functional without any internet connection, ensuring data is always accessible.
*   **Class & Subject Management:**
    *   Create and manage distinct classes (e.g., "BSc IT, 2025 Intake").
    *   Define subjects with unique codes and names (e.g., "CS101 - Intro to Programming").
    *   Assign subjects to one or more classes.
*   **Student Management:**
    *   Enroll students with a unique Registration Number and name.
    *   Assign students to their respective classes.
    *   Instantly search for any student using their Registration Number.
*   **Grade Entry & Calculation:**
    *   Record scores for various assessment components (Assignments, Mid-Semesters, Final Exams).
    *   The system handles weighted calculations automatically (though weighting is currently implicit).
*   **Data Sharing:**
    *   Generate a formatted text summary of all student results for a specific subject.
    *   Share this summary easily via external apps like WhatsApp or email using Android's native share functionality.
*   **Data Visualization:**
    *   A dashboard provides an at-a-glance overview of class performance distribution using charts.

## Technical Implementation

This project is built using modern Android development practices and a clean architectural approach.

### 1. Architecture & Technologies

*   **Language:** **Kotlin** - The official, modern language for Android development.
*   **User Interface:** **Jetpack Compose** - A declarative UI toolkit for building native interfaces with less code and powerful tools.
*   **Database:** **SQLite** - A C-language library that implements a small, fast, self-contained, high-reliability, full-featured, SQL database engine. Managed via a custom `DatabaseHelper` class.
*   **Data Access Layer:** A `DataAccessObject` (DAO) class is implemented to act as an abstraction layer between the UI and the database. This centralizes all database operations (CRUD) and ensures a clean separation of concerns.
*   **Navigation:** **Jetpack Navigation for Compose** is used to handle all screen transitions within the app, providing a consistent and predictable user experience.
*   **Charting:** **MPAndroidChart** is used to render performance charts on the dashboard, providing valuable visual feedback.

### 2. Database Schema

The local SQLite database is structured with several interconnected tables to maintain data integrity and relationships:

*   `TABLE_CLASSES`: Stores class information (`ID`, `Name`).
*   `TABLE_STUDENTS`: Stores student details (`ID`, `Reg_Number`, `Name`).
*   `TABLE_SUBJECTS`: Stores subject details (`ID`, `Code`, `Name`).
*   `TABLE_GRADES`: A central table linking students, subjects, and classes, while storing scores for each assessment component.
*   **Junction Tables:**
    *   `TABLE_CLASS_STUDENTS`: Manages the many-to-many relationship between classes and students.
    *   `TABLE_CLASS_SUBJECTS`: Manages the many-to-many relationship between classes and subjects.

### 3. Key Code Components

*   **`DatabaseHelper.kt`**: Manages the creation and versioning of the SQLite database and its tables.
*   **`DataAccessObject.kt`**: Contains all SQL logic for inserting, updating, deleting, and retrieving data. All UI components interact with this class, never directly with the database.
*   **`MainActivity.kt`**: The main entry point of the app. It sets up the navigation host, manages state for dialogs, and orchestrates the overall UI flow.
*   **Composables (`/composables` package)**: The UI is broken down into modular and reusable screens (`ClassScreen`, `SubjectScreen`, `StudentScreen`, `Dashboard`), each responsible for a specific feature set.

##  How to Build and Run

1.  Clone this repository.
2.  Open the project in Android Studio (latest stable version recommended).
3.  Let Gradle sync and download the required dependencies.
4.  Build and run the application on an Android emulator or a physical device (API 26+).

## Future Enhancements
*   **Data Backup & Restore:** Implement a feature to back up the SQLite database to cloud storage (e.g., Google Drive) and restore it.
*   **Advanced Weighting:** Allow lecturers to define custom weightings for each assessment component directly within the app.
*   **Modernize Database Access:** Migrate from the manual `DatabaseHelper` and `DAO` to **Room Persistence Library** for compile-time SQL query verification, less boilerplate code, and easier integration with Kotlin Coroutines.


