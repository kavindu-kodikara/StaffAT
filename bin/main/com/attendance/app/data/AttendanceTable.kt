package com.attendance.app.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object AttendanceTable : IntIdTable("attendance") {
    val employeeId = reference("employee_id", EmployeesTable, onDelete = ReferenceOption.CASCADE)
    val date = date("date")
    val status = varchar("status", 50) // "Present", "Absent", "Leave"
    val leaveEmailLink = varchar("leave_email_link", 1024).nullable()
    val note = text("note").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
    
    init {
        uniqueIndex("unique_attendance_per_day", employeeId, date)
    }
}
