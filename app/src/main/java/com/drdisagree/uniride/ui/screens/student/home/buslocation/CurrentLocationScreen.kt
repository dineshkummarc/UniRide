package com.drdisagree.uniride.ui.screens.student.home.buslocation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.extension.Container
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@HomeNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun CurrentLocationScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = "Current Location",
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                MapViewContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                )
            }
        )
    }
}

@Composable
private fun MapViewContent(
    modifier: Modifier
) {
    val context = LocalContext.current
    val cameraState = rememberCameraPositionState()
    val marker = LatLng(23.8041, 90.4152)
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    LaunchedEffect(key1 = marker) {
        cameraState.centerOnLocation(marker)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraState,
        uiSettings = uiSettings,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.map_style
            )
        )
    ) {
        Marker(
            state = MarkerState(position = marker),
            title = "Position",
            snippet = "Bus is currently at this location",
            draggable = true
        )
    }
}

private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        15f
    ),
    durationMs = 1500
)