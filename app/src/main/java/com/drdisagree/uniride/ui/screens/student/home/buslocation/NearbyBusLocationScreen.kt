package com.drdisagree.uniride.ui.screens.student.home.buslocation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.enums.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StarRatingBar
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.student.account.StudentSignInViewModel
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.ColorUtils.getSchedulePillColors
import com.drdisagree.uniride.utils.Formatter.getFormattedTime
import com.drdisagree.uniride.utils.toBitmapDescriptor
import com.drdisagree.uniride.viewmodels.LocationSharingViewModel
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
import com.google.maps.android.compose.Polyline
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
                    title = stringResource(R.string.current_location),
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
    reviewSubmissionViewModel: ReviewSubmissionViewModel = hiltViewModel(),
    locationViewModel: LocationSharingViewModel = hiltViewModel()
) {
    LaunchedEffect(busId) {
        nearbyBusLocationViewModel.startListening(busId)
    }

    val runningBus by nearbyBusLocationViewModel.runningBus.observeAsState()
    val routePoints by nearbyBusLocationViewModel.routePoints.observeAsState(emptyList())

    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }
    val marker = runningBus?.currentlyAt?.let { LatLng(it.latitude, it.longitude) }
    var zoomLevel by rememberSaveable { mutableFloatStateOf(15f) }
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
        if (marker != null && myMarker != null) {
            val builder = LatLngBounds.Builder()
            builder.include(marker)
            builder.include(myMarker!!)

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(builder.build(), 200),
                1_000
            )
        } else {
            marker?.let {
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(it.latitude, it.longitude))
                    .zoom(zoomLevel)
                    .build()

                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    1_000
                )
            }
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

    var showMapRoute by remember { mutableStateOf(false) }
    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }
    val openInfoDialog = remember { mutableStateOf(false) }
    val openReviewDialog = remember { mutableStateOf(false) }

    LaunchedEffect(reviewSubmissionViewModel.state) {
        reviewSubmissionViewModel.state.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        "Thank you for your review!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Error -> {
                    showLoadingDialog = false

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

    Box(
        modifier = modifier
    ) {
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
                    title = runningBus?.bus?.name ?: stringResource(R.string.unknown_name),
                    snippet = runningBus?.category?.name
                        ?: stringResource(R.string.unknown_category),
                    draggable = false,
                    icon = toBitmapDescriptor(context, R.drawable.ic_pin_map_bus_colored)
                )
            }
            if (myMarker != null) {
                Marker(
                    state = MarkerState(position = myMarker!!),
                    title = stringResource(R.string.me),
                    snippet = stringResource(R.string.my_position),
                    draggable = false,
                    icon = toBitmapDescriptor(context, R.drawable.ic_pin_map_person)
                )
            }
            if (routePoints.isNotEmpty() && showMapRoute) {
                Polyline(
                    points = routePoints,
                    color = Color.Blue,
                    width = 10f
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
                contentDescription = stringResource(R.string.driver_info),
                tint = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            if (marker != null && myMarker != null) {
                IconButton(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.medium1)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Transparent)
                        .align(Alignment.End)
                        .size(56.dp),
                    onClick = {
                        showMapRoute = !showMapRoute
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.8f),
                        painter = painterResource(id = R.drawable.img_show_route),
                        contentDescription = stringResource(R.string.show_route),
                        contentScale = ContentScale.FillBounds
                    )
                }
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
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.bus_has_reached_its_destination)
                ) {
                    navigator.navigateUp()
                }
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

        BusDetailsDialog(
            openInfoDialog = openInfoDialog,
            openReviewDialog = openReviewDialog,
            imageRequest = imageRequest,
            runningBus = runningBus,
            reviewSubmissionViewModel = reviewSubmissionViewModel
        )

        ReviewDialog(
            openReviewDialog = openReviewDialog,
            runningBus = runningBus,
            reviewSubmissionViewModel = reviewSubmissionViewModel
        )
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

    LaunchedEffect(isMapLoaded, showMapRoute, myMarker, marker) {
        if (isMapLoaded && showMapRoute && myMarker != null && marker != null) {
            nearbyBusLocationViewModel.fetchRoute(
                origin = myMarker!!,
                destination = marker
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

    if (showLoadingDialog) {
        LoadingDialog()
    }
}

@Composable
fun ReviewDialog(
    openReviewDialog: MutableState<Boolean>,
    runningBus: RunningBus?,
    reviewSubmissionViewModel: ReviewSubmissionViewModel,
    studentSignInViewModel: StudentSignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var rating by remember { mutableIntStateOf(1) }
    var review by remember { mutableStateOf("") }

    if (openReviewDialog.value) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(MaterialTheme.spacing.medium3),
            onDismissRequest = {
                openReviewDialog.value = false
            },
            title = { Text(text = stringResource(R.string.rate_driver)) },
            text = {
                Column {
                    StarRatingBar(
                        rating = rating,
                        onRatingChanged = {
                            rating = it
                        },
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.small2)
                    )
                    StyledTextField(
                        placeholder = stringResource(R.string.add_a_comment),
                        onValueChange = { review = it },
                        inputText = review,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = false,
                        minLines = 5
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (review.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please add a review",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    } else if (review.length < 20) {
                        Toast.makeText(
                            context,
                            "Review is too short",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    openReviewDialog.value = false

                    reviewSubmissionViewModel.submitReview(
                        student = studentSignInViewModel.getSignedInStudent(),
                        driver = runningBus?.driver ?: return@Button,
                        reviewMessage = review,
                        rating = rating
                    )
                }) {
                    Text(stringResource(R.string.submit))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openReviewDialog.value = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            properties = DialogProperties()
        )
    }
}

@Composable
private fun BusDetailsDialog(
    openInfoDialog: MutableState<Boolean>,
    openReviewDialog: MutableState<Boolean>,
    imageRequest: ImageRequest,
    runningBus: RunningBus?,
    reviewSubmissionViewModel: ReviewSubmissionViewModel
) {
    val context = LocalContext.current
    val (_, categoryTextColor) = remember(runningBus?.category?.name) {
        getSchedulePillColors(runningBus?.category?.name ?: context.getString(R.string.unknown))
    }
    val summaryState by reviewSubmissionViewModel.summary.collectAsState()

    LaunchedEffect(runningBus?.driver?.id) {
        reviewSubmissionViewModel.fetchSummary(runningBus?.driver?.id)
    }

    if (openInfoDialog.value) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(MaterialTheme.spacing.medium3),
            onDismissRequest = {
                openInfoDialog.value = false
            },
            title = { Text(text = stringResource(R.string.bus_details)) },
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
                                contentDescription = stringResource(R.string.profile_picture),
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
                                        append(stringResource(R.string.driver_info_colon))
                                    }
                                    append("\n")
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium
                                        )
                                    ) {
                                        append(
                                            runningBus?.driver?.name
                                                ?: stringResource(R.string.unknown_driver)
                                        )
                                    }
                                },
                                fontSize = 15.sp,
                                color = Black
                            )
                            Text(
                                text = if (runningBus?.driver?.contactPhone.isNullOrEmpty()) {
                                    if (runningBus?.driver?.contactEmail.isNullOrEmpty()) {
                                        stringResource(R.string.no_contact_info)
                                    } else {
                                        runningBus?.driver?.contactEmail
                                    }
                                } else {
                                    runningBus?.driver?.contactPhone
                                } ?: stringResource(R.string.no_contact_info),
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
                                append(stringResource(R.string.bus_name_colon))
                            }
                            append(runningBus?.bus?.name ?: stringResource(R.string.unknown))
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
                                append(stringResource(R.string.category_colon))
                            }
                            withStyle(
                                SpanStyle(
                                    color = categoryTextColor
                                )
                            ) {
                                append(
                                    runningBus?.category?.name ?: stringResource(R.string.unknown)
                                )
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
                                append(stringResource(R.string.status_colon))
                            }
                            append(
                                when (runningBus?.status) {
                                    BusStatus.RUNNING -> stringResource(R.string.driving_to_destination)
                                    BusStatus.STOPPED -> stringResource(R.string.reached_destination)
                                    else -> stringResource(R.string.waiting_for_students)
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
                                append(stringResource(R.string.departed_from_colon))
                            }
                            append(
                                runningBus?.departedFrom?.name ?: stringResource(R.string.unknown)
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
                                append(stringResource(R.string.destination_colon))
                            }
                            append(runningBus?.departedTo?.name ?: stringResource(R.string.unknown))
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
                                append(stringResource(R.string.departed_at_colon))
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
                                append(stringResource(R.string.seat_occupancy_colon))
                            }
                            append(
                                if (runningBus?.busFull == true) {
                                    stringResource(R.string.all_seats_occupied)
                                } else {
                                    stringResource(R.string.few_seats_available)
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
                                append(stringResource(R.string.review_ai_summarized_colon))
                            }
                            append(
                                when (summaryState) {
                                    is Resource.Loading -> stringResource(R.string.loading)
                                    is Resource.Success -> (summaryState as Resource.Success<String>).data
                                    is Resource.Error -> (summaryState as Resource.Error).message
                                    else -> stringResource(R.string.no_reviews_yet)
                                }
                            )
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    openInfoDialog.value = false
                }) {
                    Text(stringResource(R.string.close))
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
                            openReviewDialog.value = true
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RateReview,
                        contentDescription = stringResource(R.string.leave_a_review),
                        tint = Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.leave_a_review),
                        color = Black
                    )
                }
            },
            properties = DialogProperties()
        )
    }
}