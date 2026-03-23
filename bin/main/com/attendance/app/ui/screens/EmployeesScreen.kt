package com.attendance.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.attendance.app.domain.Employee
import com.attendance.app.repository.EmployeeRepositoryImpl
import com.attendance.app.ui.components.*
import com.attendance.app.ui.navigation.NavigationState
import com.attendance.app.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun EmployeesScreen(navigationState: NavigationState) {
    val repository = remember { EmployeeRepositoryImpl() }
    val scope = rememberCoroutineScope()
    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<Employee?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadEmployees() {
        scope.launch {
            employees = repository.getAllEmployees()
        }
    }

    LaunchedEffect(Unit) {
        loadEmployees()
    }

    if (showDialog && employeeToEdit != null) {
        EmployeeFormDialog(
            employeeToEdit = employeeToEdit!!,
            isLoading = isSaving,
            onDismiss = { if (!isSaving) showDialog = false },
            onSave = { emp ->
                scope.launch {
                    isSaving = true
                    // Update local-only fields
                    repository.updateEmployee(emp)
                    loadEmployees()
                    showDialog = false
                    isSaving = false
                }
            }
        )
    }

    var onboardingFilter by remember { mutableStateOf<String?>(null) }
    
    val filteredEmployees = remember(employees, onboardingFilter) {
        if (onboardingFilter == null) employees
        else employees.filter { it.onboardingStatus == onboardingFilter }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Employee Directory",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage your team and onboarding tracking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                PrimaryButton(
                    text = "Sync from Supabase",
                    onClick = {
                        scope.launch {
                            isSaving = true
                            repository.syncEmployeesFromSupabase()
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Employees synced successfully!")
                                    loadEmployees()
                                }
                                .onFailure {
                                    snackbarHostState.showSnackbar("Sync failed: ${it.message}")
                                }
                            isSaving = false
                        }
                    },
                    icon = Icons.Default.Refresh,
                    enabled = !isSaving
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = onboardingFilter == null,
                onClick = { onboardingFilter = null },
                label = { Text("All Staff") }
            )
            FilterChip(
                selected = onboardingFilter == "signed_in_office",
                onClick = { onboardingFilter = "signed_in_office" },
                label = { Text("Signed In") }
            )
            FilterChip(
                selected = onboardingFilter == "pending_office_signing",
                onClick = { onboardingFilter = "pending_office_signing" },
                label = { Text("Pending") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ModernTable(
            items = filteredEmployees,
            modifier = Modifier.fillMaxSize(),
            columns = listOf(
                TableColumn("Employee", weight = 2f) { emp ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { 
                            navigationState.navigateTo(Screen.EmployeeProfile, emp.id)
                        }
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    emp.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = emp.name, 
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = emp.email ?: "No email",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                TableColumn("Context", weight = 1.5f) { emp ->
                    Column {
                        Text(emp.whatsappNumber ?: "-", style = MaterialTheme.typography.bodyMedium)
                        Text("NIC: " + (emp.nicNumber ?: "-"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                TableColumn("Onboarding", weight = 1.2f) { emp ->
                    StatusBadge(emp.onboardingStatus)
                },
                TableColumn("Actions", weight = 1f) { emp ->
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { 
                            navigationState.navigateTo(Screen.EmployeeProfile, emp.id)
                        }) {
                            Icon(Icons.Default.AccountCircle, "View", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { 
                            employeeToEdit = emp
                            showDialog = true
                        }) {
                            Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = {
                            scope.launch {
                                repository.deleteEmployee(emp.id)
                                loadEmployees()
                            }
                        }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            )
        )
    }
}}
