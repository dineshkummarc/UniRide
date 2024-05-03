package com.drdisagree.uniride.ui.screens.driver.home

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ContainerNavDrawer
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.RequestLocationPermission
import com.drdisagree.uniride.ui.components.views.TopAppBarWithNavDrawerIcon
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.utils.viewmodels.GpsStateManager
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun DriverHome(
    navigator: DestinationsNavigator
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ContainerNavDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope
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
}

@Composable
private fun HandlePermissions(
    gpsStateManager: GpsStateManager = hiltViewModel()
) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(false)
    }
    val gpsRequested by remember {
        mutableStateOf(gpsStateManager.gpsRequested.value)
    }

    LaunchedEffect(Unit) {
        Prefs.putString(WHICH_USER_COLLECTION, DRIVER_COLLECTION)
        permissionGranted = areLocationPermissionsGranted(context)
    }

    RequestLocationPermission(
        onPermissionGranted = { permissionGranted = true },
        onPermissionDenied = { permissionGranted = false }) {
        Toast.makeText(
            context,
            "Please grant location permission",
            Toast.LENGTH_SHORT
        ).show()
    }

    if (permissionGranted && !gpsRequested) {
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