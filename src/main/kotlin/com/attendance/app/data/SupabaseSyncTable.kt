package com.attendance.app.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object SupabaseSyncTable : IntIdTable("supabase_sync") {
    val employeeId = reference("employee_id", EmployeesTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val supabaseAuthId = varchar("supabase_auth_id", 100).nullable()
    val syncStatus = varchar("sync_status", 50).default("Pending")
    val lastSyncedAt = datetime("last_synced_at").nullable()
    val loginStatus = varchar("login_status", 50).default("No Login") // "No Login", "Created"
    val syncErrorMessage = text("sync_error_message").nullable()
    
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}
