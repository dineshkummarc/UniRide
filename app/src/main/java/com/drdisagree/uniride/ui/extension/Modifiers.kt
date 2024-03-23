package com.drdisagree.uniride.ui.extension

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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