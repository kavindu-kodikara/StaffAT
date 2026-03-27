package com.attendance.app.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate
import java.time.LocalDateTime

object StudentResponsesTable : IntIdTable("student_responses") {
    val timestamp = varchar("timestamp", 100)
    val studentName = varchar("student_name", 255)
    val nic = varchar("nic", 50).nullable()
    val address = text("address").nullable()
    val whatsappNumber = varchar("whatsapp_number", 50).nullable()
    val contactNumber = varchar("contact_number", 50).nullable()
    val databaseName = varchar("database_name", 255).nullable()
    val counselorName = varchar("counselor_name", 255)
    val employeeId = reference("employee_id", EmployeesTable).nullable()
    val importDate = date("import_date").clientDefault { LocalDate.now() }
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    init {
        // Unique constraint for de-duplication: studentName + counselorName + importDate might not be enough
        // The user suggested Timestamp + Student Name + Counselor Name
        // But since we want day-by-day view and handle re-imports, 
        // a more robust unique check will be done in the service layer.
    }
}
