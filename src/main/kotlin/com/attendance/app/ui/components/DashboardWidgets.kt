package com.attendance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attendance.app.domain.ActivityType
import com.attendance.app.domain.DashboardActivityItem
import com.attendance.app.ui.theme.*
import java.time.format.DateTimeFormatter

@Composable
fun KPICard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    SaaSCard(
        modifier = modifier,
        padding = 20.dp,
        elevation = 2.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun AttendanceOverviewBlock(
    present: Int,
    absent: Int,
    leave: Int,
    modifier: Modifier = Modifier
) {
    val total = (present + absent + leave).coerceAtLeast(1)
    val presentRate = (present.toFloat() / total)
    
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Text("Attendance Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusTile("Present", present.toString(), color_status_present, Modifier.weight(1f))
                StatusTile("Absent", absent.toString(), color_status_absent, Modifier.weight(1f))
                StatusTile("Leave", leave.toString(), color_status_leave, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Daily Presence Rate", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = presentRate,
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = color_status_present,
                trackColor = color_status_present.copy(alpha = 0.1f)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(presentRate * 100).toInt()}% Attendance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("$present / $total Employees", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatusTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun RecentActivityPanel(
    activities: List<DashboardActivityItem>,
    modifier: Modifier = Modifier
) {
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (activities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No recent activity", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    activities.take(6).forEach { activity ->
                        ActivityItemRow(activity)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItemRow(activity: DashboardActivityItem) {
    Row(verticalAlignment = Alignment.Top) {
        val icon = when (activity.type) {
            ActivityType.ATTENDANCE -> Icons.Default.CheckCircle
            ActivityType.EMPLOYEE -> Icons.Default.PersonAdd
            ActivityType.SYSTEM -> Icons.Default.Settings
        }
        val color = when (activity.type) {
            ActivityType.ATTENDANCE -> color_status_present
            ActivityType.EMPLOYEE -> chart_color_1
            ActivityType.SYSTEM -> Color.Gray
        }
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(activity.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(activity.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                activity.time.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun QuickActionGrid(
    onAddEmployee: () -> Unit,
    onMarkAttendance: () -> Unit,
    onViewReports: () -> Unit,
    onExportCSV: () -> Unit,
    modifier: Modifier = Modifier
) {
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard("Add Employee", Icons.Default.PersonAdd, chart_color_1, Modifier.weight(1f), onAddEmployee)
                ActionCard("Attendance", Icons.Default.HowToReg, color_status_present, Modifier.weight(1f), onMarkAttendance)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard("Analytics", Icons.Default.BarChart, chart_color_2, Modifier.weight(1f), onViewReports)
                ActionCard("Export CSV", Icons.Default.FileDownload, chart_color_3, Modifier.weight(1f), onExportCSV)
            }
        }
    }
}

@Composable
fun ActionCard(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OnboardingPanel(
    pendingEmployees: List<com.attendance.app.domain.Employee>,
    modifier: Modifier = Modifier
) {
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PendingActions, null, tint = chart_color_5, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pending Onboarding", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                ColoredBadge(pendingEmployees.size.toString(), chart_color_5)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            if (pendingEmployees.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("All employees signed in!", color = color_status_present, fontWeight = FontWeight.Medium)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingEmployees.take(5).forEach { employee ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(chart_color_5, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(employee.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Pending", style = MaterialTheme.typography.labelSmall, color = chart_color_5)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayAttendanceList(
    attendanceDetails: List<Pair<com.attendance.app.domain.Employee, String?>>,
    onViewProfile: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Today, null, tint = color_status_present, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Today's Presence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (attendanceDetails.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No employees registered", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attendanceDetails.take(10).forEach { (employee, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(paddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(employee.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text(employee.email ?: "No email", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            val (statusLabel, statusColor) = when(status) {
                                "Present" -> "Present" to color_status_present
                                "Absent" -> "Absent" to color_status_absent
                                "Leave" -> "Leave" to color_status_leave
                                else -> "Not Marked" to Color.Gray
                            }
                            
                            ColoredBadge(statusLabel, statusColor)
                            
                            IconButton(onClick = { onViewProfile(employee.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlySummaryPanel(
    monthStats: com.attendance.app.domain.MonthlyStats,
    modifier: Modifier = Modifier
) {
    SaaSCard(modifier = modifier, padding = 20.dp) {
        Column {
            Text("${monthStats.monthName} Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryMetric("Total Present", monthStats.totalPresent.toString(), color_status_present)
                SummaryMetric("Total Absent", monthStats.totalAbsent.toString(), color_status_absent)
                SummaryMetric("Total Leave", monthStats.totalLeave.toString(), color_status_leave)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Monthly Efficiency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${monthStats.attendanceRate.toInt()}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("Overall Attendance Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SummaryMetric(label: String, value: String, color: Color) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ColoredBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}
