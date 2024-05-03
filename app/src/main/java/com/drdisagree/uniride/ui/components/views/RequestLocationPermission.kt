package com.drdisagree.uniride.ui.components.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority

/**
 * Composable function to request location permissions and handle different scenarios.
 *
 * @param onPermissionGranted Callback to be executed when all requested permissions are granted.
 * @param onPermissionDenied Callback to be executed when any requested permission is denied.
 * @param onPermissionsRevoked Callback to be executed when previously granted permissions are revoked.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermissionsRevoked: () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(key1 = permissionState) {
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size

        val permissionsToRequest = permissionState.permissions.filter {
            !it.hasPermission
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionState.launchMultiplePermissionRequest()
        }

        if (allPermissionsRevoked) {
            onPermissionsRevoked()
        } else {
            if (permissionState.allPermissionsGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }
}

@Composable
fun RequestGpsEnable(
    context: Context,
    onGpsEnabled: () -> Unit,
    onGpsDisabled: () -> Unit,
) {
    val locationSettingsClient = LocationServices.getSettingsClient(context)

    val requestGpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onGpsEnabled()
        } else {
            onGpsDisabled()
        }
    }

    LaunchedEffect(Unit) {
        locationSettingsClient.checkLocationSettings(
            LocationSettingsRequest.Builder()
                .addLocationRequest(
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 50L)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(10L)
                        .setMaxUpdateDelayMillis(100L)
                        .build()
                ).build()
        ).addOnSuccessListener { response ->
            val states = response.locationSettingsStates
            if (states?.isLocationPresent == true) {
                onGpsEnabled()
            }
        }.addOnFailureListener { exception ->
            val statusCode = (exception as ResolvableApiException).statusCode

            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                val intentSender = exception.resolution.intentSender
                val intent = IntentSenderRequest.Builder(intentSender).build()
                requestGpsLauncher.launch(intent)
            }
        }
    }
}

fun areLocationPermissionsGranted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}