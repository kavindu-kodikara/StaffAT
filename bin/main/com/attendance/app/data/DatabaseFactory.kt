package com.attendance.app.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        // Use a local SQLite database file in the project directory for development
        // For production, this could be configured to an AppData directory
        val dbFile = File("attendance.db")
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        
        Database.connect(url, "org.sqlite.JDBC")
        
        transaction {
            SchemaUtils.create(EmployeesTable, AttendanceTable)
        }
    }
}
