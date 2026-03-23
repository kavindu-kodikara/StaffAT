package com.attendance.app.data

import com.attendance.app.domain.Employee
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate

object SupabaseService {

    // Next.js API Configuration loaded from config.properties
    private val API_BASE_URL = AppConfig.apiBaseUrl
    private val API_ADMIN_TOKEN = AppConfig.apiAdminToken

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private fun getFullUrl(path: String): String {
        val base = API_BASE_URL.removeSuffix("/")
        val cleanPath = path.removePrefix("/")
        return "$base/$cleanPath"
    }





    /**
     * Fetches attendance records for a given date range via the Next.js API proxy.
     */
    suspend fun fetchAttendance(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SupabaseAttendance> = withContext(Dispatchers.IO) {
        try {
            val response: AttendanceResponse = client.get(getFullUrl("api/admin/attendance")) {
                header("X-Admin-Token", API_ADMIN_TOKEN)
                parameter("startDate", startDate.toString())
                parameter("endDate", endDate.toString())
            }.body()
            
            if (response.success) response.data else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @Serializable
    data class AttendanceResponse(
        val success: Boolean,
        val data: List<SupabaseAttendance>
    )

    @Serializable
    data class SupabaseEmployee(
        val id: String,
        @SerialName("full_name") val fullName: String,
        val nic: String,
        val mobile: String,
        @SerialName("employee_code") val employeeCode: String? = null,
        @SerialName("sheet_link") val sheetLink: String? = null,
        @SerialName("created_at") val createdAt: String? = null
    )

    @Serializable
    data class EmployeeListResponse(
        val success: Boolean,
        val data: List<SupabaseEmployee>
    )

    /**
     * Fetches all employees from Supabase via the Next.js API proxy.
     */
    suspend fun fetchEmployees(): List<SupabaseEmployee> = withContext(Dispatchers.IO) {
        try {
            val response: EmployeeListResponse = client.get(getFullUrl("api/admin/staff")) {
                header("X-Admin-Token", API_ADMIN_TOKEN)
            }.body()
            
            if (response.success) response.data else emptyList()
        } catch (e: Exception) {
            println("Error fetching employees: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}

@Serializable
data class SupabaseAttendance(
    val id: String? = null,
    @SerialName("employee_id") val supabaseAuthId: String,
    val date: String,
    val timestamp: String? = null
)