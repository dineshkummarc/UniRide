package com.drdisagree.uniride.ui.components.views

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.drdisagree.uniride.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun NoInternetDialog(context: Context, onDismiss: () -> Unit) {
    var dialogVisible by rememberSaveable { mutableStateOf(true) }

    if (dialogVisible) {
        DisposableEffect(Unit) {
            MaterialAlertDialogBuilder(context)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setIcon(R.drawable.ic_no_internet)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    onDismiss()
                }
                .show()
            onDispose {
                dialogVisible = false
            }
        }
    }
}