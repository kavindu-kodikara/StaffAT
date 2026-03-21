package com.attendance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.attendance.app.ui.theme.*

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "Present" -> color_status_present to "Present"
        "Absent" -> color_status_absent to "Absent"
        "Leave" -> color_status_leave to "Leave"
        "signed_in_office" -> color_status_signed_in to "Signed in Office"
        "pending_office_signing" -> color_status_pending to "Pending Office Signing"
        "Premium" -> chart_color_1 to "Premium"
        "Standard" -> MaterialTheme.colorScheme.primary to "Standard"
        "Low" -> color_status_absent to "Low"
        else -> MaterialTheme.colorScheme.outline to status.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }


    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
