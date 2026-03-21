package com.attendance.app.repository

import com.attendance.app.data.EmployeesTable
import com.attendance.app.domain.Employee
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class EmployeeRepositoryImpl : EmployeeRepository {
    private fun ResultRow.toEmployee() = Employee(
        id = this[EmployeesTable.id].value,
        name = this[EmployeesTable.name],
        email = this[EmployeesTable.email],
        whatsappNumber = this[EmployeesTable.whatsappNumber],
        nicNumber = this[EmployeesTable.nicNumber],
        address = this[EmployeesTable.address],
        googleSheetLink = this[EmployeesTable.googleSheetLink],
        internalComment = this[EmployeesTable.internalComment],
        onboardingStatus = this[EmployeesTable.onboardingStatus],
        createdAt = this[EmployeesTable.createdAt],
        updatedAt = this[EmployeesTable.updatedAt]
    )

    override suspend fun getAllEmployees(): List<Employee> = newSuspendedTransaction {
        EmployeesTable.selectAll().map { it.toEmployee() }
    }

    override suspend fun getEmployeeById(id: Int): Employee? = newSuspendedTransaction {
        EmployeesTable.select { EmployeesTable.id eq id }.map { it.toEmployee() }.singleOrNull()
    }

    override suspend fun addEmployee(employee: Employee): Int = newSuspendedTransaction {
        EmployeesTable.insertAndGetId {
            it[name] = employee.name
            it[email] = employee.email
            it[whatsappNumber] = employee.whatsappNumber
            it[nicNumber] = employee.nicNumber
            it[address] = employee.address
            it[googleSheetLink] = employee.googleSheetLink
            it[internalComment] = employee.internalComment
            it[onboardingStatus] = employee.onboardingStatus
            // createdAt and updatedAt handled by clientDefault
        }.value
    }

    override suspend fun updateEmployee(employee: Employee): Boolean = newSuspendedTransaction {
        val updatedRows = EmployeesTable.update({ EmployeesTable.id eq employee.id }) {
            it[name] = employee.name
            it[email] = employee.email
            it[whatsappNumber] = employee.whatsappNumber
            it[nicNumber] = employee.nicNumber
            it[address] = employee.address
            it[googleSheetLink] = employee.googleSheetLink
            it[internalComment] = employee.internalComment
            it[onboardingStatus] = employee.onboardingStatus
            it[updatedAt] = java.time.LocalDateTime.now()
        }
        updatedRows > 0
    }

    override suspend fun deleteEmployee(id: Int): Boolean = newSuspendedTransaction {
        EmployeesTable.deleteWhere { EmployeesTable.id eq id } > 0
    }

    override suspend fun getOnboardingStatusBreakdown(): Map<String, Int> = newSuspendedTransaction {
        EmployeesTable
            .slice(EmployeesTable.onboardingStatus, EmployeesTable.onboardingStatus.count())
            .selectAll()
            .groupBy(EmployeesTable.onboardingStatus)
            .associate { it[EmployeesTable.onboardingStatus] to it[EmployeesTable.onboardingStatus.count()].toInt() }
    }

    override suspend fun getEmployeesByOnboardingStatus(status: String): List<Employee> = newSuspendedTransaction {
        EmployeesTable.select { EmployeesTable.onboardingStatus eq status }.map { it.toEmployee() }
    }
}
