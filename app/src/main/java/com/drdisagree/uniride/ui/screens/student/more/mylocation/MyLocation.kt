package com.drdisagree.uniride.ui.screens.student.more.mylocation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.components.views.isGpsEnabled
import com.drdisagree.uniride.utils.AnimationQueue
import com.drdisagree.uniride.utils.sensorRotationEffect
import com.drdisagree.uniride.utils.toBitmapDescriptor
import com.drdisagree.uniride.viewmodels.LocationSharingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun MyLocation(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.my_location_title),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                MapView(
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun MapView(
    paddingValues: PaddingValues,
    locationViewModel: LocationSharingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }
    var marker: LatLng? by rememberSaveable { mutableStateOf(null) }
    val location by locationViewModel.locationFlow.collectAsState()

    LaunchedEffect(location) {
        location?.let {
            if (marker == null || marker != LatLng(it.latitude, it.longitude)) {
                marker = LatLng(it.latitude, it.longitude)
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(23.8161532, 90.2747436), 15f)
    }
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = false,
            myLocationButtonEnabled = false
        )
    }
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )
        )
    }

    val degrees = sensorRotationEffect(context)
    var zoomLevel by rememberSaveable { mutableFloatStateOf(15f) }

    LaunchedEffect(
        key1 = true
    ) {
        val locationPermissionGranted = areLocationPermissionsGranted(context)
        val locationEnabled = isGpsEnabled(context)

        if (!locationPermissionGranted) {
            Toast.makeText(
                context,
                "Location permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (!locationEnabled) {
            Toast.makeText(
                context,
                "GPS is not enabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(key1 = marker, key2 = degrees) {
        marker?.let {
            val cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                )
                .zoom(zoomLevel)
                .bearing(degrees.toFloat())
                .build()

            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                1_000
            )
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.position.zoom }
            .collect { zoom ->
                if (zoomLevel != zoom) {
                    zoomLevel = zoom
                }
            }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = mapProperties,
        onMapLoaded = {
            isMapLoaded = true
        }
    ) {
        if (marker != null) {
            val scope = rememberCoroutineScope()
            val markerState = rememberMarkerState(position = marker!!)
            val updatePosition = { pos: LatLng -> markerState.position = pos }
            val animationQueue = AnimationQueue(markerState.position, scope, updatePosition)

            LaunchedEffect(marker) {
                animationQueue.addToQueue(marker!!, 0f)
            }

            val markerBitmap = remember {
                toBitmapDescriptor(context, R.drawable.ic_pin_map_person)
            }

            Marker(
                state = markerState,
                title = stringResource(R.string.me),
                snippet = stringResource(R.string.my_position),
                draggable = false,
                icon = markerBitmap
            )
        }
    }

    if (!isMapLoaded) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = !isMapLoaded,
            enter = EnterTransition.None,
            exit = fadeOut()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .background(Color.White)
                    .wrapContentSize()
            )
        }
    }
}
