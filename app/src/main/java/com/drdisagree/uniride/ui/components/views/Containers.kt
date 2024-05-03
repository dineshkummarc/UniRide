package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun Container(
    modifier: Modifier = Modifier,
    shadow: Boolean = true,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = if (shadow) MaterialTheme.spacing.medium1 else 0.dp
    ) {
        content()
    }
}

@Composable
fun ContainerNavDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    shadow: Boolean = true,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = if (shadow) MaterialTheme.spacing.medium1 else 0.dp
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.widthIn(max = 320.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerTonalElevation = 0.dp,
                    drawerShape = RoundedCornerShape(0.dp)
                ) {
                    drawerContent()
                }
            },
            gesturesEnabled = drawerState.isOpen
        ) {
            content()
        }
    }
}