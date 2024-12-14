package com.drdisagree.uniride.ui.screens.student.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Notice
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.RESOURCE_INFO_URL
import com.drdisagree.uniride.data.utils.Constant.STUDENT_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.HomeHeader
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.RequestLocationPermission
import com.drdisagree.uniride.ui.components.views.RequestNotificationPermission
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.components.views.isNotificationPermissionGranted
import com.drdisagree.uniride.ui.screens.destinations.NearbyBusLocationScreenDestination
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.DistanceUtils.distance
import com.drdisagree.uniride.utils.Formatter.getFormattedTime
import com.drdisagree.uniride.viewmodels.GeocodingViewModel
import com.drdisagree.uniride.viewmodels.GpsStateManager
import com.drdisagree.uniride.viewmodels.LocationSharingViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import eu.wewox.textflow.TextFlow
import java.util.Locale

@HomeNavGraph(start = true)
@Destination(style = FadeInOutTransition::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {},
            content = { paddingValues ->
                HomeContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun HomeContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues,
    noticeBoardViewModel: NoticeBoardViewModel = hiltViewModel(),
    nearbyBusesViewModel: NearbyBusesViewModel = hiltViewModel(),
    locationViewModel: LocationSharingViewModel = hiltViewModel(),
    geocodingViewModel: GeocodingViewModel = hiltViewModel()
) {
    HandlePermissions()

    val notices by noticeBoardViewModel.noticeBoard.collectAsState(initial = Resource.Unspecified())
    val buses by nearbyBusesViewModel.runningBuses.observeAsState(emptyList())
    val location by locationViewModel.locationFlow.collectAsState()

    val sortedBuses = location?.let { loc ->
        buses.sortedBy { bus ->
            val busLat = bus.currentlyAt?.latitude ?: 0.0
            val busLng = bus.currentlyAt?.longitude ?: 0.0
            distance(loc.latitude, loc.longitude, busLat, busLng)
        }
    } ?: buses

    val distances by nearbyBusesViewModel.distances.observeAsState(emptyMap())
    val defaultLocationName = stringResource(R.string.retrieving)
    val locationNames by geocodingViewModel.locationNames.observeAsState(emptyMap())
    val showLoadingDialog by rememberUpdatedState(nearbyBusesViewModel.state is Resource.Loading<*>)

    LaunchedEffect(location, buses) {
        nearbyBusesViewModel.fetchAndStoreDistances(location, buses)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        item {
            HomeHeader()
        }

        item {
            Text(
                text = stringResource(R.string.announcement),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium1,
                    end = MaterialTheme.spacing.medium1,
                    top = MaterialTheme.spacing.small2
                )
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.medium1,
                        end = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1
                    )
                    .clip(MaterialTheme.shapes.large)
                    .background(Blue)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_intersecting_waves_scattered),
                    colorFilter = ColorFilter.tint(color = Color(0xFF3163C6)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.medium2)
                ) {
                    when (notices) {
                        is Resource.Loading -> {
                            Text(
                                text = stringResource(R.string.loading),
                                color = Color.White,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Justify
                            )
                        }

                        is Resource.Success -> {
                            (notices as Resource.Success<Notice>).data?.let {
                                TextFlow(
                                    text = it.announcement,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.img_announcement),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(color = Color.White),
                                        modifier = Modifier
                                            .padding(end = MaterialTheme.spacing.small3)
                                            .width(60.dp)
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            (notices as Resource.Error<Notice>).message?.let {
                                Text(
                                    text = it,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.our_resources),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium1,
                    end = MaterialTheme.spacing.medium1,
                    top = MaterialTheme.spacing.medium2
                )
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.medium1,
                        end = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        icon = Icons.Default.DirectionsBus,
                        count = stringResource(R.string.total_vehicles_count),
                        label = stringResource(R.string.total_vehicles),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatCard(
                        icon = Icons.Default.Person,
                        count = stringResource(R.string.drivers_helpers_count),
                        label = stringResource(R.string.drivers_helpers),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium1))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        icon = Icons.Default.Route,
                        count = stringResource(R.string.total_routes_count),
                        label = stringResource(R.string.total_routes),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatCard(
                        icon = Icons.Default.Star,
                        count = stringResource(R.string.technicians_count),
                        label = stringResource(R.string.technicians),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.nearby_buses),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.medium2,
                    start = MaterialTheme.spacing.medium1,
                    bottom = MaterialTheme.spacing.medium1
                )
            )
        }

        itemsIndexed(
            sortedBuses,
            key = { index, _ -> index }
        ) { index, bus ->
            val locationName = locationNames[bus.uuid] ?: defaultLocationName

            LaunchedEffect(bus.currentlyAt?.latitude, bus.currentlyAt?.longitude) {
                geocodingViewModel.fetchLocationName(
                    bus.uuid,
                    bus.currentlyAt?.latitude,
                    bus.currentlyAt?.longitude
                )
            }

            NearbyBusListItem(
                index = index,
                runningBus = bus,
                onClick = {
                    navigator.navigate(
                        NearbyBusLocationScreenDestination(
                            busId = bus.uuid
                        )
                    )
                },
                currentLocationName = if (distances[bus.uuid] != null && locationName != defaultLocationName) {
                    String.format(
                        Locale.getDefault(),
                        "%s (%.1fkm)", locationName, distances[bus.uuid]
                    )
                } else {
                    locationName
                }
            )
        }

        if (showLoadingDialog || sortedBuses.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = MaterialTheme.spacing.large3,
                            bottom = MaterialTheme.spacing.extraLarge2
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (showLoadingDialog) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(Color.White)
                                .wrapContentSize()
                        )
                    } else if (sortedBuses.isEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bus_sideview),
                            contentDescription = null,
                            modifier = Modifier.width(160.dp),
                            contentScale = ContentScale.FillWidth,
                            colorFilter = ColorFilter.tint(color = DarkGray.copy(alpha = 0.5f))
                        )
                        Text(
                            text = stringResource(R.string.no_bus_found_nearby),
                            color = DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall2)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyBusListItem(
    modifier: Modifier = Modifier,
    index: Int,
    currentLocationName: String,
    runningBus: RunningBus,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick?.invoke()
            }
    ) {
        if (index != 0) {
            HorizontalDivider(
                color = LightGray,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium3,
                    vertical = MaterialTheme.spacing.medium1
                ),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = stringResource(R.string.map_with_marker_image),
                modifier = Modifier
                    .padding(end = 16.dp, top = 2.dp)
                    .size(28.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .fillMaxHeight()
                    .padding(end = MaterialTheme.spacing.medium1),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = runningBus.bus.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${runningBus.departedFrom!!.name} <> ${runningBus.departedTo!!.name}",
                    color = Dark,
                    fontSize = 14.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        ) {
                            append(stringResource(R.string.now_colon))
                        }
                        append(
                            currentLocationName
                        )
                    },
                    color = Dark,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.departed),
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = getFormattedTime(context, runningBus.departedAt),
                    color = Dark,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun HandlePermissions(
    gpsStateManager: GpsStateManager = hiltViewModel()
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember {
        mutableStateOf(false)
    }
    val gpsRequested by remember {
        mutableStateOf(gpsStateManager.gpsRequested.value)
    }
    var notificationPermissionGranted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        Prefs.putString(WHICH_USER_COLLECTION, STUDENT_COLLECTION)
        Firebase.messaging
            .subscribeToTopic(STUDENT_COLLECTION)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to $STUDENT_COLLECTION"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to $STUDENT_COLLECTION"
                }
                Log.d("FCM", msg)
            }

        locationPermissionGranted = areLocationPermissionsGranted(context)
        notificationPermissionGranted = isNotificationPermissionGranted(context)
    }

    if (!locationPermissionGranted) {
        RequestLocationPermission(
            onPermissionGranted = { locationPermissionGranted = true },
            onPermissionDenied = { locationPermissionGranted = false }
        ) {
            Toast.makeText(
                context,
                "Please grant location permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (locationPermissionGranted && !notificationPermissionGranted) {
        RequestNotificationPermission(
            onPermissionGranted = { notificationPermissionGranted = true },
            onPermissionDenied = { notificationPermissionGranted = false }
        )
    }

    if (locationPermissionGranted && !gpsRequested) {
        RequestGpsEnable(
            context = context,
            onGpsEnabled = { },
            onGpsDisabled = {
                Toast.makeText(
                    context,
                    "Please enable GPS",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        gpsStateManager.setGpsRequested(true)
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, count: String, label: String) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.small2))
            .clickable {
                try {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(RESOURCE_INFO_URL)
                        }
                    )
                } catch (e: Exception) {
                    Toast
                        .makeText(context, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Black.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = count,
                fontSize = 20.sp,
                color = Blue,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                fontSize = 15.sp,
                color = Black,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}