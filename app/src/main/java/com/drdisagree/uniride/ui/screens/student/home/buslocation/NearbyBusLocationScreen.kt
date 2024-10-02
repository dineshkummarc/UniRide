package com.drdisagree.uniride.ui.screens.student.home.buslocation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.ColorUtils.getSchedulePillColors
import com.drdisagree.uniride.utils.Formatter.getFormattedTime
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
import kotlinx.coroutines.Dispatchers

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
                    navigator = navigator,
                    busId = busId
                )
            }
        )
    }
}

@Composable
private fun MapViewContent(
    modifier: Modifier,
    navigator: DestinationsNavigator,
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

    val openInfoDialog = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Box() {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
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

            IconButton(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium1)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Black.copy(alpha = 0.5f))
                    .align(Alignment.TopEnd),
                onClick = {
                    openInfoDialog.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Driver Info",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(36.dp)
                )
            }

            if (runningBus?.status == BusStatus.STOPPED) {
                ButtonPrimary(
                    modifier = Modifier
                        .padding(
                            start = MaterialTheme.spacing.medium1,
                            end = MaterialTheme.spacing.medium1,
                            top = MaterialTheme.spacing.medium1,
                            bottom = MaterialTheme.spacing.large2
                        )
                        .align(Alignment.BottomCenter),
                    text = "Bus has reached its destination"
                ) {
                    navigator.navigateUp()
                }
            }

            val placeholder by remember { mutableIntStateOf(R.drawable.img_profile_pic_default) }
            val imageUrl = runningBus?.driver?.profileImage

            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(imageUrl + "_low")
                .diskCacheKey(imageUrl + "_low")
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .crossfade(250)
                .size(256)
                .build()

            BusDetailsDialog(openInfoDialog, imageRequest, runningBus)
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

@Composable
private fun BusDetailsDialog(
    openInfoDialog: MutableState<Boolean>,
    imageRequest: ImageRequest,
    runningBus: RunningBus?
) {
    val context = LocalContext.current
    val (_, categoryTextColor) = remember(runningBus?.category?.name) {
        getSchedulePillColors(runningBus?.category?.name ?: "Unknown")
    }

    if (openInfoDialog.value) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(MaterialTheme.spacing.medium3),
            onDismissRequest = {
                openInfoDialog.value = false
            },
            title = { Text(text = "Bus Details") },
            text = {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small3),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(100))
                                .background(Gray)
                                .padding(MaterialTheme.spacing.extraSmall2)
                        ) {
                            AsyncImage(
                                model = imageRequest,
                                placeholder = painterResource(id = R.drawable.img_loading),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(100)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append("Driver Info:")
                                    }
                                    append("\n")
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium
                                        )
                                    ) {
                                        append(runningBus?.driver?.name ?: "Unknown driver")
                                    }
                                },
                                fontSize = 15.sp,
                                color = Black
                            )
                            Text(
                                text = if (runningBus?.driver?.contactPhone.isNullOrEmpty()) {
                                    if (runningBus?.driver?.contactEmail.isNullOrEmpty()) {
                                        "No contact info"
                                    } else {
                                        runningBus?.driver?.contactEmail
                                    }
                                } else {
                                    runningBus?.driver?.contactPhone
                                } ?: "No contact info",
                                fontSize = 14.sp
                            )
                        }
                    }
                    HorizontalDivider(
                        color = LightGray,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium1)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Bus Name: ")
                            }
                            append(runningBus?.bus?.name ?: "Unknown")
                        },
                        fontSize = 15.sp
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Category: ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = categoryTextColor
                                )
                            ) {
                                append(runningBus?.category?.name ?: "Unknown")
                            }
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Status: ")
                            }
                            append(
                                when (runningBus?.status) {
                                    BusStatus.RUNNING -> "Driving to destination"
                                    BusStatus.STOPPED -> "Reached destination"
                                    else -> "Waiting for students"
                                }
                            )
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Departed From: ")
                            }
                            append(runningBus?.departedFrom?.name ?: "Unknown")
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Destination: ")
                            }
                            append(runningBus?.departedTo?.name ?: "Unknown")
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Departed At: ")
                            }
                            append(getFormattedTime(context, runningBus?.departedAt))
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Seat Occupancy: ")
                            }
                            append(
                                "Seat Left"
                            )
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("Driver Review: ")
                            }
                            append(
                                "No reviews yet"
                            )
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    openInfoDialog.value = false
                }) {
                    Text("Close")
                }
            },
            dismissButton = {
                Row(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .clip(CircleShape)
                        .border(width = 1.dp, color = LightGray, shape = CircleShape)
                        .clickable {
                            openInfoDialog.value = false
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RateReview,
                        contentDescription = "Leave a review",
                        tint = Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Leave a review",
                        color = Black
                    )
                }
            },
            properties = DialogProperties()
        )
    }
}