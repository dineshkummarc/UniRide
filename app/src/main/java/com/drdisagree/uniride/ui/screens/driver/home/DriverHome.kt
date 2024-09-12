package com.drdisagree.uniride.ui.screens.driver.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Bus
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ContainerNavDrawer
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.RequestLocationPermission
import com.drdisagree.uniride.ui.components.views.RequestNotificationPermission
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TopAppBarWithNavDrawerIcon
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.components.views.isGpsEnabled
import com.drdisagree.uniride.ui.components.views.isNotificationPermissionGranted
import com.drdisagree.uniride.ui.screens.destinations.BusLocationDestination
import com.drdisagree.uniride.ui.screens.destinations.EditProfileScreenDestination
import com.drdisagree.uniride.ui.screens.driver.home.navdrawer.NavigationDrawer
import com.drdisagree.uniride.ui.screens.global.viewmodels.GetDriverViewModel
import com.drdisagree.uniride.ui.screens.global.viewmodels.ListsViewModel
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.GpsStateManager
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun DriverHome(
    navigator: DestinationsNavigator,
    getDriverViewModel: GetDriverViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ContainerNavDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                navigator = navigator,
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                getDriverViewModel = getDriverViewModel
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBarWithNavDrawerIcon(
                    title = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.Cursive,
                    onNavigationIconClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            content = { paddingValues ->
                DriverHomeContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun DriverHomeContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator
) {
    HandlePermissions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IncompleteProfileWarn(navigator = navigator)

        ShareLocationFields(navigator = navigator)
    }
}

@Composable
private fun IncompleteProfileWarn(navigator: DestinationsNavigator) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1
            )
            .clip(MaterialTheme.shapes.medium)
            .background(LightGray)
            .padding(MaterialTheme.spacing.medium1)
    ) {
        val incompleteProfileWarn =
            "Some information on your account appears to be missing or incomplete. Please update your information to avoid account suspension or penalty. "
        val editProfile = "» Edit Profile «"

        val annotatedString = buildAnnotatedString {
            withStyle(SpanStyle(color = Dark, fontSize = 14.sp)) {
                pushStringAnnotation(
                    tag = incompleteProfileWarn,
                    annotation = incompleteProfileWarn
                )
                append(incompleteProfileWarn)
            }
            withStyle(SpanStyle(color = Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold)) {
                pushStringAnnotation(tag = editProfile, annotation = editProfile)
                append(editProfile)
            }
        }

        Column {
            Text(
                text = "Your profile is incomplete.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.small2)
            )

            @Suppress("DEPRECATION")
            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = editProfile,
                        start = offset,
                        end = offset
                    )
                        .firstOrNull()?.let {
                            navigator.navigate(EditProfileScreenDestination)
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Justify
                )
            )
        }
    }
}

@Composable
private fun ShareLocationFields(
    navigator: DestinationsNavigator,
    listsViewModel: ListsViewModel = hiltViewModel(),
    driverViewModel: GetDriverViewModel = hiltViewModel(),
    driverHomeViewModel: DriverHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val busList by listsViewModel.busModels.collectAsState()
    val busCategoryList by listsViewModel.busCategoryModels.collectAsState()
    val placeList by listsViewModel.placeModels.collectAsState()

    val defaultBusName = Bus(
        name = "Select Bus"
    )
    val defaultBusCategory = BusCategory(
        name = "Bus Category"
    )
    val defaultFrom = Place(
        name = "Start From"
    )
    val defaultTo = Place(
        name = "Destination"
    )

    var selectedBus by rememberSaveable { mutableStateOf(defaultBusName) }
    var busCategory by rememberSaveable { mutableStateOf(defaultBusCategory) }
    var locationFrom by rememberSaveable { mutableStateOf(defaultFrom) }
    var locationTo by rememberSaveable { mutableStateOf(defaultTo) }

    LaunchedEffect(driverViewModel.getDriver) {
        driverViewModel.getDriver.collect { resource ->
            if (resource is Resource.Success) {
                driverHomeViewModel.checkIfAnyBusAssignedToDriver(resource.data) { isAssigned ->
                    if (isAssigned) {
                        navigator.navigate(BusLocationDestination)
                    }
                }
            }
        }
    }

    Text(
        text = "Let's start driving",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
    )

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1
            ),
        selectedText = selectedBus.name,
        itemList = busList.sortedWith(compareBy<Bus> {
            it.name.substringBefore('-')
        }.thenBy {
            val suffix = it.name.substringAfter('-', "")
            if (suffix.isEmpty()) Int.MAX_VALUE else suffix.toIntOrNull() ?: Int.MAX_VALUE
        }).map {
            it.name
        }.toTypedArray(),
        onItemSelected = {
            selectedBus = busList.first { bus ->
                bus.name == it
            }
        },
        fillMaxWidth = true
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small2))

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1
            ),
        selectedText = busCategory.name,
        itemList = busCategoryList.map {
            it.name
        }.toTypedArray(),
        onItemSelected = {
            busCategory = busCategoryList.first { category ->
                category.name == it
            }
        },
        fillMaxWidth = true
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small2))

    StyledDropDownMenu(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1),
        selectedText = locationFrom.name,
        itemList = placeList.map {
            it.name
        }.toTypedArray(),
        onItemSelected = {
            locationFrom = placeList.first { place ->
                place.name == it
            }
        },
        fillMaxWidth = true
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small2))

    StyledDropDownMenu(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1),
        selectedText = locationTo.name,
        itemList = placeList.map {
            it.name
        }.toTypedArray(),
        onItemSelected = {
            locationTo = placeList.first { place ->
                place.name == it
            }
        },
        fillMaxWidth = true
    )

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small2))

    ButtonPrimary(
        text = "Start Sharing Location",
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.medium1)
            .fillMaxWidth()
    ) {
        if (selectedBus.name == defaultBusName.name ||
            busCategory.name == defaultBusCategory.name ||
            locationFrom.name == defaultFrom.name ||
            locationTo.name == defaultTo.name
        ) {
            Toast.makeText(
                context,
                "Please select all fields",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        } else if (locationFrom == locationTo) {
            Toast.makeText(
                context,
                "Both locations cannot be the same",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        if (!areLocationPermissionsGranted(context)) {
            Toast.makeText(
                context,
                "Please grant location permission",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        } else if (!isGpsEnabled(context)) {
            Toast.makeText(
                context,
                "Please enable GPS",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        } else if (!isNotificationPermissionGranted(context)) {
            Toast.makeText(
                context,
                "Please grant notification permission",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        driverHomeViewModel.startDeparture(
            driverViewModel = driverViewModel,
            listsViewModel = listsViewModel,
            busName = selectedBus.name,
            fromPlaceName = locationFrom.name,
            toPlaceName = locationTo.name,
            categoryName = busCategory.name
        )
    }

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(driverHomeViewModel.updateBusStatus) {
        driverHomeViewModel.updateBusStatus.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    navigator.navigate(BusLocationDestination)
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
        Prefs.putString(WHICH_USER_COLLECTION, DRIVER_COLLECTION)
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