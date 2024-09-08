package com.drdisagree.uniride.ui.components.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Composable function to request notification permissions and handle different scenarios.
 *
 * @param onPermissionGranted Callback to be executed when the notification permission is granted.
 * @param onPermissionDenied Callback to be executed when the notification permission is denied.
 */
@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current

    val isAndroid13OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    if (!isAndroid13OrLater) {
        onPermissionGranted()
        return
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    val permission = Manifest.permission.POST_NOTIFICATIONS

    LaunchedEffect(key1 = permission) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(permission)
        } else {
            onPermissionGranted()
        }
    }
}

/**
 * Function to check if notification permission is granted.
 *
 * @param context The context of the calling component.
 * @return True if the permission is granted or if the Android version is below 13, false otherwise.
 */
fun isNotificationPermissionGranted(context: Context): Boolean {
    // Check if the device runs on Android 13 or later
    val isAndroid13OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    if (!isAndroid13OrLater) {
        return true
    }

    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}
