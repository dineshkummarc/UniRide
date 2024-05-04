package com.drdisagree.uniride.ui.screens.student.route.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.screens.admin.account.AccountStatusViewModel
import com.drdisagree.uniride.ui.screens.destinations.RouteScreenDestination
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun EditRoute(
    navigator: DestinationsNavigator,
    route: Route,
    editRouteViewModel: EditRouteViewModel = hiltViewModel()
) {
    var openDialog by remember { mutableStateOf(false) }

    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(R.string.edit_route),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = "Logout",
                                tint = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    },
                    endIconClick = {
                        openDialog = true
                    }
                )
            },
            content = { paddingValues ->
                EditRouteContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    route = route,
                    editRouteViewModel = editRouteViewModel,
                    openDialog = openDialog,
                    onCloseDialog = {
                        openDialog = false
                    }
                )
            }
        )
    }
}

@Composable
private fun EditRouteContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    route: Route,
    editRouteViewModel: EditRouteViewModel,
    openDialog: Boolean,
    onCloseDialog: () -> Unit,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel()
) {
    var isAdminState by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(key1 = accountStatusViewModel.isAdmin) {
        val isAdminLiveData = accountStatusViewModel.isAdmin
        val observer = Observer<Boolean?> { isAdmin ->
            isAdminState = isAdmin
        }
        isAdminLiveData.observeForever(observer)

        onDispose {
            isAdminLiveData.removeObserver(observer)
        }
    }

    when (isAdminState) {
        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(Color.White)
                        .wrapContentSize()
                )
            }
        }

        true -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.medium1)
            ) {
                EditRouteFields(
                    route = route,
                    navigator = navigator,
                    editRouteViewModel = editRouteViewModel,
                    openDialog = openDialog,
                    onCloseDialog = onCloseDialog
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "You are not an admin"
                )
            }
        }
    }
}

@Composable
private fun EditRouteFields(
    route: Route,
    navigator: DestinationsNavigator,
    editRouteViewModel: EditRouteViewModel,
    openDialog: Boolean,
    onCloseDialog: () -> Unit
) {
    val context = LocalContext.current
    var routeNo by rememberSaveable { mutableStateOf(route.routeNo) }
    var startTime by rememberSaveable { mutableStateOf(route.startTime) }
    var routeName by rememberSaveable { mutableStateOf(route.routeName) }
    var routeDetails by rememberSaveable { mutableStateOf(route.routeDetails) }
    var departureTime by rememberSaveable { mutableStateOf(route.departureTime) }
    var routeMap by rememberSaveable { mutableStateOf(route.routeWebUrl ?: "") }

    StyledTextField(
        placeholder = "Route No",
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small2),
        onValueChange = { routeNo = it },
        inputText = routeNo,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    StyledTextField(
        placeholder = "Start Time (To DSC) (Separated by $$)",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { startTime = it },
        inputText = startTime,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )

    StyledTextField(
        placeholder = "Route Name",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { routeName = it },
        inputText = routeName,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    StyledTextField(
        placeholder = "Route Details (Locations separated by <>)",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { routeDetails = it },
        inputText = routeDetails,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )

    StyledTextField(
        placeholder = "Departure Time (From DSC) (Separated by $$)",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { departureTime = it },
        inputText = departureTime,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )

    StyledTextField(
        placeholder = "Route Map (Google Maps URL)",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { routeMap = it },
        inputText = routeMap,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    ButtonPrimary(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth(),
        text = "Submit"
    ) {
        if (
            routeNo.isEmpty() ||
            startTime.isEmpty() ||
            routeName.isEmpty() ||
            routeDetails.isEmpty() ||
            departureTime.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Please fill in all fields",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        editRouteViewModel.editRoute(
            route.copy(
                routeNo = routeNo,
                startTime = startTime,
                routeName = routeName,
                routeDetails = routeDetails,
                departureTime = departureTime,
                routeWebUrl = routeMap.ifEmpty { null }
            )
        )
    }

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        editRouteViewModel.editState.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        result.data,
                        Toast.LENGTH_SHORT
                    ).show()

                    navigator.popBackStack(
                        route = RouteScreenDestination,
                        inclusive = false
                    )
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

    LaunchedEffect(Unit) {
        editRouteViewModel.deleteState.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        result.data,
                        Toast.LENGTH_SHORT
                    ).show()

                    navigator.popBackStack(
                        route = RouteScreenDestination,
                        inclusive = false
                    )
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

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                onCloseDialog()
            },
            title = {
                Text(text = "Are you sure?")
            },
            text = {
                Text("This action cannot be undone. Delete this route?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCloseDialog()
                        editRouteViewModel.deleteRoute(route.uuid)
                    }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCloseDialog()
                    }) {
                    Text("Cancel")
                }
            }
        )
    }
}