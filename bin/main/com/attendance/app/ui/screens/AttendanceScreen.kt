package com.attendance.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.attendance.app.domain.AttendanceRecord
import com.attendance.app.domain.Employee
import com.attendance.app.repository.AttendanceRepositoryImpl
import com.attendance.app.repository.EmployeeRepositoryImpl
import com.attendance.app.ui.components.*
import com.attendance.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceScreen() {
    val employeeRepo = remember { EmployeeRepositoryImpl() }
    val attendanceRepo = remember { AttendanceRepositoryImpl() }
    val scope = rememberCoroutineScope()
    
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var attendanceRecords by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    
    var showLeaveDialog by remember { mutableStateOf(false) }
    var selectedEmployeeForLeave by remember { mutableStateOf<Employee?>(null) }
    var leaveEmailLink by remember { mutableStateOf("") }

    fun loadData() {
        scope.launch {
            employees = employeeRepo.getAllEmployees()
            attendanceRecords = attendanceRepo.getAttendanceByDate(selectedDate)
        }
    }

    LaunchedEffect(selectedDate) {
        loadData()
    }

    if (showLeaveDialog && selectedEmployeeForLeave != null) {
        SaaSModal("Mark Leave", onDismissRequest = { showLeaveDialog = false }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Enter leave email link for ${selectedEmployeeForLeave?.name}")
                SaaSOutlinedTextField(
                    value = leaveEmailLink,
                    onValueChange = { leaveEmailLink = it },
                    label = "Leave Email Link"
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton("Cancel", onClick = { showLeaveDialog = false })
                    Spacer(Modifier.width(8.dp))
                    PrimaryButton("Save Leave", onClick = {
                        scope.launch {
                            attendanceRepo.markAttendance(
                                AttendanceRecord(
                                    employeeId = selectedEmployeeForLeave!!.id,
                                    date = selectedDate,
                                    status = "Leave",
                                    leaveEmailLink = leaveEmailLink
                                )
                            )
                            loadData()
                            showLeaveDialog = false
                        }
                    })
                }
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    
    val filteredEmployees = remember(employees, searchQuery) {
        employees.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Daily Attendance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Manage daily presence and leave records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { selectedDate = selectedDate.minusDays(1) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ChevronLeft, "Previous Day", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                SaaSCard(padding = 12.dp, elevation = 2.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { selectedDate = selectedDate.plusDays(1) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ChevronRight, "Next Day", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryButton(text = "Go to Today", onClick = { selectedDate = LocalDate.now() })
                Spacer(modifier = Modifier.width(16.dp))
                var isSyncing by remember { mutableStateOf(false) }
                PrimaryButton(
                    text = if (isSyncing) "Syncing..." else "Sync from Supabase", 
                    onClick = {
                        scope.launch {
                            isSyncing = true
                            com.attendance.app.data.AttendanceSyncService().syncFromSupabase(selectedDate, selectedDate)
                            loadData()
                            isSyncing = false
                        }
                    },
                    enabled = !isSyncing,
                    icon = if (isSyncing) null else Icons.Default.Sync
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        SaaSOutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search employees...",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ModernTable(
            items = filteredEmployees,
            modifier = Modifier.fillMaxSize(),
            columns = listOf(
                TableColumn("Employee", weight = 2f) { emp ->
                    Column {
                        Text(emp.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text(emp.onboardingStatus.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                TableColumn("Status", weight = 3f) { emp ->
                    val record = attendanceRecords.find { it.employeeId == emp.id }
                    val currentStatus = record?.status ?: ""
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatusChip(
                            text = "Present", 
                            isSelected = currentStatus == "Present",
                            activeColor = color_status_present,
                            onClick = {
                                scope.launch {
                                    attendanceRepo.markAttendance(
                                        AttendanceRecord(employeeId = emp.id, date = selectedDate, status = "Present")
                                    )
                                    loadData()
                                }
                            }
                        )
                        StatusChip(
                            text = "Absent", 
                            isSelected = currentStatus == "Absent",
                            activeColor = color_status_absent,
                            onClick = {
                                scope.launch {
                                    attendanceRepo.markAttendance(
                                        AttendanceRecord(employeeId = emp.id, date = selectedDate, status = "Absent")
                                    )
                                    loadData()
                                }
                            }
                        )
                        StatusChip(
                            text = "Leave", 
                            isSelected = currentStatus == "Leave",
                            activeColor = color_status_leave,
                            onClick = {
                                selectedEmployeeForLeave = emp
                                leaveEmailLink = record?.leaveEmailLink ?: ""
                                showLeaveDialog = true
                            }
                        )
                    }
                }
            )
        )
    }
}

@Composable
fun StatusChip(text: String, isSelected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) activeColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isSelected) activeColor else MaterialTheme.colorScheme.outlineVariant))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
