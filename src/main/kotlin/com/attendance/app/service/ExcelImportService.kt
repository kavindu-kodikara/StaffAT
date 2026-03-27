package com.attendance.app.service

import com.attendance.app.domain.StudentResponse
import com.attendance.app.repository.EmployeeRepository
import com.attendance.app.repository.StudentResponseRepository
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.time.LocalDate

class ExcelImportService(
    private val employeeRepository: EmployeeRepository,
    private val studentResponseRepository: StudentResponseRepository
) {
    suspend fun importStudentResponses(filePath: String): Result<ImportResult> {
        return try {
            val employees = employeeRepository.getAllEmployees()
            val workbook: Workbook = XSSFWorkbook(FileInputStream(filePath))
            val sheet: Sheet = workbook.getSheetAt(0)
            
            var importedCount = 0
            var skippedCount = 0
            var duplicateCount = 0

            // Header mapping
            // Timestamp, Student Name, NIC, Address, Whatsapp Number, Contact Number 01, Database name, Counselor Name
            
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                
                val timestamp = getCellValue(row.getCell(0))
                val studentName = getCellValue(row.getCell(1))
                val nic = getCellValue(row.getCell(2))
                val address = getCellValue(row.getCell(3))
                val whatsapp = getCellValue(row.getCell(4))
                val contact01 = getCellValue(row.getCell(5))
                val dbName = getCellValue(row.getCell(6))
                val counselorName = getCellValue(row.getCell(7))

                if (studentName.isEmpty() || counselorName.isEmpty()) {
                    skippedCount++
                    continue
                }

                // Filtering: Match counselorName against our employees
                // Format: Employee Name + Employee ID (e.g. Madushika Weragama G14-A01)
                val matchedEmployee = employees.find { emp ->
                    val fullNameWithCode = if (emp.employeeCode != null) "${emp.name} ${emp.employeeCode}" else emp.name
                    // Direct match or partial match logic
                    counselorName.equals(fullNameWithCode, ignoreCase = true) || 
                    (emp.employeeCode != null && counselorName.contains(emp.employeeCode!!))
                }

                if (matchedEmployee != null) {
                    // De-duplication check
                    if (studentResponseRepository.exists(timestamp, studentName, counselorName)) {
                        duplicateCount++
                    } else {
                        val response = StudentResponse(
                            timestamp = timestamp,
                            studentName = studentName,
                            nic = nic,
                            address = address,
                            whatsappNumber = whatsapp,
                            contactNumber = contact01,
                            databaseName = dbName,
                            counselorName = counselorName,
                            employeeId = matchedEmployee.id,
                            importDate = LocalDate.now()
                        )
                        studentResponseRepository.save(response)
                        importedCount++
                    }
                } else {
                    skippedCount++
                }
            }

            workbook.close()
            Result.success(ImportResult(importedCount, skippedCount, duplicateCount))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun getCellValue(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue.trim()
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    cell.localDateTimeCellValue.toString()
                } else {
                    val value = cell.numericCellValue
                    if (value == value.toLong().toDouble()) {
                        value.toLong().toString()
                    } else {
                        value.toString()
                    }
                }
            }
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            org.apache.poi.ss.usermodel.CellType.FORMULA -> cell.cellFormula
            else -> ""
        }
    }
}

data class ImportResult(
    val imported: Int,
    val skipped: Int,
    val duplicates: Int
)
