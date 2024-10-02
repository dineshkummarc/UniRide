package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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

    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var autoFocus by remember { mutableStateOf(false) }

    LaunchedEffect(timer) {
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
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Enter OTP") },
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    otpValues.forEachIndexed { index, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    otpValues[index] = newValue
                                    if (newValue.isNotEmpty()) {
                                        if (index < 5) {
                                            autoFocus = true
                                            focusRequesters[index + 1].requestFocus()
                                        } else {
                                            focusManager.clearFocus()
                                        }
                                    }
                                } else {
                                    val diffChar = newValue.firstOrNull { it != value.getOrNull(0) }
                                    if (diffChar != null) {
                                        otpValues[index] = diffChar.toString()
                                    }
                                    if (index < 5) {
                                        autoFocus = true
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                }
                                otp = otpValues.joinToString("")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged {
                                    if (it.isFocused && !autoFocus && otpValues[index].isNotEmpty()) {
                                        otpValues[index] = ""
                                        autoFocus = false
                                    }
                                }
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.nativeKeyEvent.keyCode == Key.Backspace.nativeKeyCode) {
                                        if (otpValues[index].isEmpty() && index > 0) {
                                            autoFocus = true
                                            focusRequesters[index - 1].requestFocus()
                                        }
                                    }
                                    false
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            maxLines = 1,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Resend OTP in $timer seconds")
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(otp) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            if (canResend) {
                TextButton(onClick = {
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
        },
        properties = DialogProperties()
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OtpInputDialog(onDismissRequest = {}, onSubmit = {}, resendOtp = {})
}