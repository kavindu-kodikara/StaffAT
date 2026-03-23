package com.attendance.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.attendance.app.domain.Employee
import com.attendance.app.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*

@Composable
fun EmployeeFormDialog(
    employeeToEdit: Employee,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf(employeeToEdit.name) }
    var email by remember { mutableStateOf(employeeToEdit.email ?: "") }
    var whatsapp by remember { mutableStateOf(employeeToEdit.whatsappNumber ?: "") }
    var nic by remember { mutableStateOf(employeeToEdit.nicNumber ?: "") }
    var status by remember { mutableStateOf(employeeToEdit.onboardingStatus) }
    var address by remember { mutableStateOf(employeeToEdit.address ?: "") }
    var googleSheetLink by remember { mutableStateOf(employeeToEdit.googleSheetLink ?: "") }
    var internalComment by remember { mutableStateOf(employeeToEdit.internalComment ?: "") }
    var employeeCode by remember { mutableStateOf(employeeToEdit.employeeCode ?: "") }
    var username by remember { mutableStateOf(employeeToEdit.username ?: "") }
    var password by remember { mutableStateOf(employeeToEdit.password ?: "") }

    SaaSModal(
        title = "Edit Employee Profile",
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            SaaSOutlinedTextField(value = employeeCode, onValueChange = { employeeCode = it }, label = "Employee ID")
            SaaSOutlinedTextField(value = name, onValueChange = { name = it }, label = "Full Name")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SaaSOutlinedTextField(value = username, onValueChange = { username = it }, label = "Username", modifier = Modifier.weight(1f))
                SaaSOutlinedTextField(value = password, onValueChange = { password = it }, label = "Password", modifier = Modifier.weight(1f))
            }
            SaaSOutlinedTextField(value = email, onValueChange = { email = it }, label = "Email Address")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SaaSOutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = "WhatsApp Number", modifier = Modifier.weight(1f))
                SaaSOutlinedTextField(value = nic, onValueChange = { nic = it }, label = "NIC Number", modifier = Modifier.weight(1f))
            }
            
            // Onboarding Status Selection
            var statusExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                SaaSOutlinedTextField(
                    value = if (status == "signed_in_office") "Signed in Office" else "Pending Office Signing",
                    onValueChange = { },
                    label = "Onboarding Status",
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { statusExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Signed in Office") },
                        onClick = {
                            status = "signed_in_office"
                            statusExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Pending Office Signing") },
                        onClick = {
                            status = "pending_office_signing"
                            statusExpanded = false
                        }
                    )
                }
            }

            SaaSOutlinedTextField(value = address, onValueChange = { address = it }, label = "Address")
            SaaSOutlinedTextField(value = googleSheetLink, onValueChange = { googleSheetLink = it }, label = "Google Sheet Link")
            SaaSOutlinedTextField(value = internalComment, onValueChange = { internalComment = it }, label = "Internal Comments", modifier = Modifier.height(100.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(text = "Cancel", onClick = onDismiss)
                Spacer(modifier = Modifier.width(16.dp))
                PrimaryButton(
                    text = if (isLoading) "Saving..." else "Save", 
                    onClick = {
                        onSave(Employee(
                            id = employeeToEdit.id,
                            name = name,
                            email = email,
                            whatsappNumber = whatsapp,
                            nicNumber = nic,
                            onboardingStatus = status,
                            address = address,
                            googleSheetLink = googleSheetLink,
                            internalComment = internalComment,
                            employeeCode = employeeCode,
                            username = username,
                            password = password
                        ))
                    }, 
                    enabled = !isLoading && name.isNotBlank() && employeeCode.isNotBlank(),
                    icon = if (isLoading) null else Icons.Default.Check
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.width(16.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}
