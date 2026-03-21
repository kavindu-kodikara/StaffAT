package com.attendance.app.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object EmployeesTable : IntIdTable("employees") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).nullable()
    val whatsappNumber = varchar("whatsapp_number", 50).nullable()
    val nicNumber = varchar("nic_number", 50).nullable()
    val address = text("address").nullable()
    val googleSheetLink = varchar("google_sheet_link", 1024).nullable()
    val internalComment = text("internal_comment").nullable()
    val onboardingStatus = varchar("onboarding_status", 50) // "signed_in_office", "pending_office_signing"
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}
