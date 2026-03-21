package com.attendance.app.repository

import com.attendance.app.domain.Employee

interface EmployeeRepository {
    suspend fun getAllEmployees(): List<Employee>
    suspend fun getEmployeeById(id: Int): Employee?
    suspend fun addEmployee(employee: Employee): Int
    suspend fun updateEmployee(employee: Employee): Boolean
    suspend fun deleteEmployee(id: Int): Boolean
    suspend fun getOnboardingStatusBreakdown(): Map<String, Int>
    suspend fun getEmployeesByOnboardingStatus(status: String): List<com.attendance.app.domain.Employee>
}
