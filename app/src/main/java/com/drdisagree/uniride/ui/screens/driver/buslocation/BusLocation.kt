package com.drdisagree.uniride.ui.screens.driver.buslocation

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.services.LocationService
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ButtonSecondary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.DisableBackHandler
import com.drdisagree.uniride.ui.components.views.KeepScreenOn
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.NoInternetDialog
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.TopAppBarNoButton
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.components.views.isGpsEnabled
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.SystemUtils.isInternetAvailable
import com.drdisagree.uniride.utils.toBitmapDescriptor
import com.drdisagree.uniride.utils.viewmodels.LocationSharingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@RootNavGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun BusLocation(
    navigator: DestinationsNavigator
) {
    DisableBackHandler(isDisabled = true) {
        Container(shadow = false) {
            Scaffold(
                topBar = {
                    TopAppBarNoButton(
                        title = "Sharing Location"
                    )
                },
                content = { paddingValues ->
                    MapView(
                        navigator = navigator,
                        paddingValues = paddingValues
                    )
                }
            )
        }
    }
}

@Composable
private fun MapView(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues,
    locationViewModel: LocationSharingViewModel = hiltViewModel(),
    busLocationViewModel: BusLocationViewModel = hiltViewModel()
) {
    KeepScreenOn()
    CheckGpsAndInternetPeriodically()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Intent(context.applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startService(this)
        }
    }

    var isMapLoaded by remember { mutableStateOf(false) }
    var marker: LatLng? by rememberSaveable { mutableStateOf(null) }
    val location by locationViewModel.locationFlow.collectAsState()
    location?.let {
        if (marker == null || marker != LatLng(it.latitude, it.longitude)) {
            marker = LatLng(it.latitude, it.longitude)
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

    val sensorManager = remember { context.getSystemService(SENSOR_SERVICE) as SensorManager }
    val isMagneticFieldSensorPresent = remember {
        sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD) != null
    }
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)
    val rotationMatrix = FloatArray(9)
    val mOrientationAngles = FloatArray(3)
    var degrees by rememberSaveable { mutableIntStateOf(0) }
    var currentTime by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var zoomLevel by rememberSaveable { mutableFloatStateOf(15f) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == TYPE_ACCELEROMETER) {
                    System.arraycopy(
                        event.values,
                        0,
                        accelerometerReading,
                        0,
                        accelerometerReading.size
                    )
                } else if (event.sensor.type == TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(
                        event.values,
                        0,
                        magnetometerReading,
                        0,
                        magnetometerReading.size
                    )
                }
                val azimuthInRadians = mOrientationAngles[0]

                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).roundToInt()

                if (currentTime + 1000 < System.currentTimeMillis()) {
                    degrees = if (azimuthInDegrees < 0) {
                        azimuthInDegrees + 360
                    } else {
                        azimuthInDegrees
                    }
                    currentTime = System.currentTimeMillis()
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )

                SensorManager.getOrientation(rotationMatrix, mOrientationAngles)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        if (isMagneticFieldSensorPresent) {
            sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)?.also { accelerometer ->
                sensorManager.registerListener(
                    sensorEventListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
            sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)?.also { magneticField ->
                sensorManager.registerListener(
                    sensorEventListener,
                    magneticField,
                    SensorManager.SENSOR_DELAY_GAME,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
        }

        onDispose {
            if (isMagneticFieldSensorPresent) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
    }

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

    LaunchedEffect(key1 = marker) {
        marker?.let {
            busLocationViewModel.updateBusLocation(location = it)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapLoaded = {
                isMapLoaded = true
            }
        ) {
            if (marker != null) {
                Marker(
                    state = MarkerState(position = marker!!),
                    title = "Me",
                    snippet = "My position",
                    draggable = false,
                    icon = toBitmapDescriptor(context, R.drawable.ic_pin_map_bus)
                )
            }
        }

        var openDialog by remember { mutableStateOf(false) }
        val runningBus by busLocationViewModel.runningBus.collectAsState()
        val status = runningBus?.status ?: BusStatus.STANDBY
        var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ButtonSecondary(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium1,
                        end = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1,
                    )
                    .fillMaxWidth(),
                text = when (runningBus?.busFull ?: false) {
                    true -> "Occupancy: All Seats Occupied"
                    false -> "Occupancy: Few Seats Available"
                }
            ) {
                when (runningBus?.busFull ?: false) {
                    true -> {
                        busLocationViewModel.updateBusStatus(busOccupied = false)
                    }

                    false -> {
                        busLocationViewModel.updateBusStatus(busOccupied = true)
                    }
                }
            }
            ButtonPrimary(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium1,
                        end = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1,
                        bottom = MaterialTheme.spacing.extraLarge1
                    )
                    .fillMaxWidth(),
                text = when (status) {
                    BusStatus.STANDBY -> "Update Status to Driving"
                    BusStatus.RUNNING -> "Stop Sharing Location"
                    BusStatus.STOPPED -> "Start Sharing Location"
                }
            ) {
                when (status) {
                    BusStatus.STANDBY -> {
                        busLocationViewModel.updateBusStatus(newStatus = BusStatus.RUNNING)
                    }

                    BusStatus.RUNNING -> {
                        openDialog = true
                    }

                    BusStatus.STOPPED -> {
                        busLocationViewModel.updateBusStatus(newStatus = BusStatus.STANDBY)
                    }
                }
            }
        }

        if (openDialog) {
            StyledAlertDialog(
                title = "Are you sure?",
                message = "This action cannot be undone. Stop sharing location?",
                confirmButtonText = "Stop",
                dismissButtonText = "Cancel",
                onConfirmButtonClick = {
                    openDialog = false
                    busLocationViewModel.stopBus { success ->
                        if (success) {
                            Intent(
                                context.applicationContext,
                                LocationService::class.java
                            ).apply {
                                action = LocationService.ACTION_STOP
                                context.startService(this)
                            }

                            navigator.navigateUp()
                        }
                    }
                },
                onDismissButtonClick = {
                    openDialog = false
                },
                onDismissRequest = {
                    openDialog = false
                }
            )
        }

        LaunchedEffect(busLocationViewModel.updateBusStatus) {
            busLocationViewModel.updateBusStatus.collect { result ->
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

        LaunchedEffect(busLocationViewModel.updateSeatOccupancy) {
            busLocationViewModel.updateSeatOccupancy.collect { result ->
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

        if (showLoadingDialog) {
            LoadingDialog()
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

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(busLocationViewModel.updateBusStatus) {
        busLocationViewModel.updateBusStatus.collect { result ->
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

    if (showLoadingDialog) {
        LoadingDialog()
    }

    LaunchedEffect(busLocationViewModel.updateBusLocation) {
        busLocationViewModel.updateBusLocation.collect { result ->
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
fun CheckGpsAndInternetPeriodically() {
    val context = LocalContext.current
    var requestGps by remember { mutableStateOf(false) }

    var isNoInternetDialogShown by rememberSaveable { mutableStateOf(false) }
    var isInternetAvailable by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            requestGps = !isGpsEnabled(context)
            isInternetAvailable = isInternetAvailable(context)

            if (!isInternetAvailable && !isNoInternetDialogShown) {
                isNoInternetDialogShown = true
            } else if (isInternetAvailable && isNoInternetDialogShown) {
                isNoInternetDialogShown = false
            }

            delay(5000)
        }
    }

    if (requestGps) {
        StyledAlertDialog(
            title = "GPS Not Enabled",
            message = "Please enable GPS to share your location.",
        )

        RequestGpsEnable(
            context = context,
            onGpsEnabled = {
                requestGps = false
            },
            onGpsDisabled = {
                Toast.makeText(
                    context,
                    "Please enable GPS",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    if (isNoInternetDialogShown) {
        NoInternetDialog(
            context = context,
            onDismiss = { isNoInternetDialogShown = false }
        )
    }
}