package com.attendance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.attendance.app.ui.navigation.NavigationState
import com.attendance.app.ui.navigation.Screen

@Composable
fun AppShell(
    navigationState: NavigationState,
    content: @Composable (PaddingValues) -> Unit
) {
    if (navigationState.currentScreen is Screen.Login) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            content(PaddingValues())
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Sidebar(
            currentScreen = navigationState.currentScreen,
            navigationState = navigationState
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = navigationState.currentScreen.title)
            
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                content(PaddingValues())
            }
        }
    }
}
