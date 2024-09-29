package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun StyledAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String? = null,
    onConfirmButtonClick: (() -> Unit)? = null,
    dismissButtonText: String? = null,
    onDismissButtonClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(MaterialTheme.spacing.medium3),
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                fontSize = 22.sp
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            if (confirmButtonText != null && onConfirmButtonClick != null) {
                Button(onClick = onConfirmButtonClick) {
                    Text(text = confirmButtonText)
                }
            }
        },
        dismissButton = {
            if (dismissButtonText != null && onDismissButtonClick != null) {
                TextButton(onClick = onDismissButtonClick) {
                    Text(text = dismissButtonText)
                }
            }
        },
        properties = properties
    )
}