package com.attendance.app.data

import com.attendance.app.domain.AttendanceRecord
import com.attendance.app.domain.AttendanceStatus
import com.attendance.app.repository.AttendanceRepository
import com.attendance.app.repository.AttendanceRepositoryImpl
import com.attendance.app.repository.EmployeeRepository
import com.attendance.app.repository.EmployeeRepositoryImpl
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

class AttendanceSyncService(
    private val employeeRepo: EmployeeRepository = EmployeeRepositoryImpl(),
    private val attendanceRepo: AttendanceRepository = AttendanceRepositoryImpl()
) {
    suspend fun syncFromSupabase(startDate: LocalDate, endDate: LocalDate): Result<Int> = try {
        val employees = employeeRepo.getAllEmployees()
        val syncMappings = getSyncMappings()
        
        // 1. Fetch from Supabase
        val remoteRecords = SupabaseService.fetchAttendance(startDate, endDate)
        
        var count = 0
        newSuspendedTransaction {
            // 2. Process remote records
            remoteRecords.forEach { remote ->
                val localEmpId = syncMappings[remote.supabaseAuthId] ?: return@forEach
                val localDate = LocalDate.parse(remote.date)
                
                // Upsert into local AttendanceTable
                val existing = AttendanceTable.select { 
                    (AttendanceTable.employeeId eq localEmpId) and (AttendanceTable.date eq localDate)
                }.singleOrNull()
                
                if (existing == null) {
                    AttendanceTable.insert {
                        it[employeeId] = localEmpId
                        it[date] = localDate
                        it[status] = AttendanceStatus.PRESENT.value
                    }
                    count++
                } else if (existing[AttendanceTable.status] == AttendanceStatus.PENDING.value) {
                    AttendanceTable.update({ (AttendanceTable.employeeId eq localEmpId) and (AttendanceTable.date eq localDate) }) {
                        it[status] = AttendanceStatus.PRESENT.value
                    }
                    count++
                }
            }
            
            // 3. Mark PENDING for missing days
            var date = startDate
            while (!date.isAfter(endDate)) {
                employees.forEach { emp ->
                    val hasRecord = AttendanceTable.select {
                        (AttendanceTable.employeeId eq emp.id) and (AttendanceTable.date eq date)
                    }.any()
                    
                    if (!hasRecord) {
                        AttendanceTable.insert {
                            it[employeeId] = emp.id
                            it[AttendanceTable.date] = date
                            it[status] = AttendanceStatus.PENDING.value
                        }
                    }
                }
                date = date.plusDays(1)
            }
        }
        Result.success(count)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    private suspend fun getSyncMappings(): Map<String, Int> = newSuspendedTransaction {
        SupabaseSyncTable.selectAll().associate { 
            it[SupabaseSyncTable.supabaseAuthId]!! to it[SupabaseSyncTable.employeeId].value 
        }
    }
}

// Extension to SupabaseService.kt if needed, but I'll add the property here for now
// Actually, it's better to keep it in SupabaseService.kt
