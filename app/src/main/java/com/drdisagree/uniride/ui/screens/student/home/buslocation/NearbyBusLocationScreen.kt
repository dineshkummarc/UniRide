package com.drdisagree.uniride.ui.screens.student.home.buslocation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.utils.toBitmapDescriptor
import com.drdisagree.uniride.utils.toBitmapDescriptorWithColor
import com.drdisagree.uniride.utils.viewmodels.LocationSharingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
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
fun NearbyBusLocationScreen(
    navigator: DestinationsNavigator,
    busId: String
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
                        .padding(paddingValues = paddingValues),
                    busId = busId
                )
            }
        )
    }
}

@Composable
private fun MapViewContent(
    modifier: Modifier,
    busId: String,
    nearbyBusLocationViewModel: NearbyBusLocationViewModel = hiltViewModel(),
    locationViewModel: LocationSharingViewModel = hiltViewModel()
) {
    LaunchedEffect(busId) {
        nearbyBusLocationViewModel.startListening(busId)
    }

    val runningBus by nearbyBusLocationViewModel.runningBus.observeAsState()

    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }
    val marker = runningBus?.currentlyAt?.let { LatLng(it.latitude, it.longitude) }
    var zoomLevel by rememberSaveable { mutableFloatStateOf(5f) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(23.8161532, 90.2747436), zoomLevel)
    }
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = true
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

    var myMarker: LatLng? by rememberSaveable { mutableStateOf(null) }
    val myLocation by locationViewModel.locationFlow.collectAsState()
    myLocation?.let {
        if (myMarker == null || myMarker != LatLng(it.latitude, it.longitude)) {
            myMarker = LatLng(it.latitude, it.longitude)
        }
    }

    LaunchedEffect(key1 = marker, key2 = myMarker) {
        if (marker != null || myMarker != null) {
            val builder = LatLngBounds.Builder()
            marker?.let { builder.include(it) }
            myMarker?.let { builder.include(it) }

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(builder.build(), 200),
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
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = mapProperties,
        onMapLoaded = {
            isMapLoaded = true
        }
    ) {
        if (marker != null) {
            Marker(
                state = MarkerState(position = marker),
                title = runningBus?.bus?.name ?: "Unknown name",
                snippet = runningBus?.category?.name ?: "Unknown category",
                draggable = false,
                icon = toBitmapDescriptor(context, R.drawable.ic_pin_map_bus_colored)
            )
        }
        if (myMarker != null) {
            Marker(
                state = MarkerState(position = myMarker!!),
                title = "Me",
                snippet = "My position",
                draggable = false,
                icon = toBitmapDescriptorWithColor(
                    context,
                    R.drawable.ic_pin_map_person,
                    Color(0xFF1E90FF)
                )
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

    LaunchedEffect(nearbyBusLocationViewModel.state) {
        nearbyBusLocationViewModel.state.collect { result ->
            when (result) {
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }
}