package com.attendance.app.domain

import java.time.LocalDate
import java.time.LocalDateTime

data class Employee(
    val id: Int = 0,
    val name: String,
    val email: String? = null,
    val whatsappNumber: String? = null,
    val nicNumber: String? = null,
    val address: String? = null,
    val googleSheetLink: String? = null,
    val internalComment: String? = null,
    val onboardingStatus: String = "pending_office_signing",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class AttendanceRecord(
    val id: Int = 0,
    val employeeId: Int,
    val date: LocalDate,
    val status: String,
    val leaveEmailLink: String? = null,
    val note: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class AttendanceStatus(val value: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LEAVE("Leave")
}

enum class OnboardingStatus(val value: String, val displayName: String) {
    PENDING("pending_office_signing", "Pending Office Signing"),
    SIGNED_IN("signed_in_office", "Signed in Office")
}

data class DashboardActivityItem(
    val title: String,
    val subtitle: String,
    val time: LocalDateTime,
    val type: ActivityType
)

enum class ActivityType {
    ATTENDANCE, EMPLOYEE, SYSTEM
}

data class MonthlyStats(
    val monthName: String,
    val totalPresent: Int,
    val totalAbsent: Int,
    val totalLeave: Int,
    val attendanceRate: Double
)

data class EmployeeAttendanceStats(
    val totalPresent: Int,
    val totalAbsent: Int,
    val totalLeave: Int,
    val attendanceRate: Double
)

