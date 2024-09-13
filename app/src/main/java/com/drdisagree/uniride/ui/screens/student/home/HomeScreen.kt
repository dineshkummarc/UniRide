package com.drdisagree.uniride.ui.screens.student.home

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import com.drdisagree.uniride.data.utils.Constant.STUDENT_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.RequestLocationPermission
import com.drdisagree.uniride.ui.components.views.RequestNotificationPermission
import com.drdisagree.uniride.ui.components.views.TopAppBarNoButton
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.components.views.isNotificationPermissionGranted
import com.drdisagree.uniride.ui.screens.destinations.NearbyBusLocationScreenDestination
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.DistanceUtils.distance
import com.drdisagree.uniride.utils.Formatter.getFormattedTime
import com.drdisagree.uniride.utils.viewmodels.GeocodingViewModel
import com.drdisagree.uniride.utils.viewmodels.GpsStateManager
import com.drdisagree.uniride.utils.viewmodels.LocationSharingViewModel
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
            topBar = {
                TopAppBarNoButton(
                    title = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.Cursive
                )
            },
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
    paddingValues: PaddingValues
) {
    HandlePermissions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        NoticeBoard()
        NearbyBuses(navigator = navigator)
    }
}

@Composable
private fun NoticeBoard(
    modifier: Modifier = Modifier,
    noticeBoardViewModel: NoticeBoardViewModel = hiltViewModel()
) {
    val notices by noticeBoardViewModel.noticeBoard.collectAsState(initial = Resource.Unspecified())

    Text(
        text = "Announcement",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium1)
            .clip(MaterialTheme.shapes.large)
            .background(Blue)
            .padding(MaterialTheme.spacing.medium2)
    ) {
        when (notices) {
            is Resource.Loading -> {
                Text(
                    text = "Loading...",
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
                                .width(80.dp)
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

@Composable
private fun NearbyBuses(
    navigator: DestinationsNavigator,
    nearbyBusesViewModel: NearbyBusesViewModel = hiltViewModel(),
    locationViewModel: LocationSharingViewModel = hiltViewModel()
) {
    Text(
        text = "Nearby Buses",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(
            top = MaterialTheme.spacing.medium1,
            start = MaterialTheme.spacing.medium1,
            bottom = MaterialTheme.spacing.medium1
        )
    )

    val context = LocalContext.current
    val buses by nearbyBusesViewModel.runningBuses.observeAsState(emptyList())
    var showLoadingDialog by rememberSaveable { mutableStateOf(true) }
    val location by locationViewModel.locationFlow.collectAsState()

    val sortedBuses = location?.let { loc ->
        buses.sortedBy { bus ->
            val busLat = bus.currentlyAt?.latitude ?: 0.0
            val busLng = bus.currentlyAt?.longitude ?: 0.0
            distance(loc.latitude, loc.longitude, busLat, busLng)
        }
    } ?: buses

    LaunchedEffect(nearbyBusesViewModel.state) {
        nearbyBusesViewModel.state.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false
                }

                is Resource.Error -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    showLoadingDialog = false
                }
            }
        }
    }

    if (sortedBuses.isEmpty()) {
        if (showLoadingDialog) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.large3),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(Color.White)
                        .wrapContentSize()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.large3),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No bus found nearby!",
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        repeat(sortedBuses.size) { index ->
            NearbyBusListItem(
                index = index,
                myLocation = location,
                runningBus = sortedBuses[index],
                onClick = {
                    navigator.navigate(
                        NearbyBusLocationScreenDestination(
                            busId = sortedBuses[index].uuid
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun NearbyBusListItem(
    modifier: Modifier = Modifier,
    index: Int,
    myLocation: Location?,
    runningBus: RunningBus,
    onClick: (() -> Unit)? = null,
    geocodingViewModel: GeocodingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val locationName by geocodingViewModel.locationName.observeAsState("Retrieving...")

    LaunchedEffect(runningBus.currentlyAt?.latitude, runningBus.currentlyAt?.longitude) {
        geocodingViewModel.fetchLocationName(
            runningBus.currentlyAt?.latitude,
            runningBus.currentlyAt?.longitude
        )
    }

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
                contentDescription = "Map with marker image",
                modifier = Modifier
                    .padding(end = 16.dp, top = 2.dp)
                    .size(28.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .padding(end = MaterialTheme.spacing.medium1),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = runningBus.bus.name,
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "${runningBus.departedFrom!!.name} <> ${runningBus.departedTo!!.name}",
                    color = Dark,
                    fontSize = 14.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Color.Black)) {
                            append("Location: ")
                        }
                        append(
                            if (locationName != null) {
                                if (myLocation != null && runningBus.currentlyAt != null) {
                                    String.format(
                                        Locale.getDefault(),
                                        "%s (%.1fkm)", locationName, distance(
                                            myLocation.latitude,
                                            myLocation.longitude,
                                            runningBus.currentlyAt.latitude,
                                            runningBus.currentlyAt.longitude
                                        )
                                    )
                                } else {
                                    locationName
                                }
                            } else {
                                "Unknown"
                            }
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
                    text = "Departed at",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
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