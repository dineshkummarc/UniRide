package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.ui.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun OtpInputDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
    resendOtp: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var timer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        } else {
            canResend = true
        }
    }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(MaterialTheme.spacing.medium3),
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(text = "Enter OTP")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Resend OTP in $timer seconds")
            }
        },
        confirmButton = {
            Button(onClick = {
                onSubmit(otp)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            if (canResend) {
                Button(onClick = {
                    resendOtp()
                    timer = 60
                    canResend = false
                }) {
                    Text("Resend OTP")
                }
            } else {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OtpInputDialog(onDismissRequest = {}, onSubmit = {}, resendOtp = {})
}