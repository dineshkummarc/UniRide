package com.drdisagree.uniride.ui.screens.admin.route.edit

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.ui.components.navigation.RoutesNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.screens.destinations.RouteDetailsScreenDestination
import com.drdisagree.uniride.ui.screens.destinations.RouteScreenDestination
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.drdisagree.uniride.utils.viewmodels.ListsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@RoutesNavGraph
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
                                contentDescription = stringResource(R.string.delete),
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
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()

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
                    text = stringResource(R.string.you_are_not_an_admin)
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
    onCloseDialog: () -> Unit,
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val routeCategoryList by listsViewModel.routeCategoryModels.collectAsState()

    var routeNo by rememberSaveable { mutableStateOf(route.routeNo) }
    var routeCategory by rememberSaveable { mutableStateOf(route.routeCategory) }
    var routeName by rememberSaveable { mutableStateOf(route.routeName) }
    var routeDetails by rememberSaveable { mutableStateOf(route.routeDetails) }
    var startTime by rememberSaveable { mutableStateOf(route.startTime) }
    var departureTime by rememberSaveable { mutableStateOf(route.departureTime) }
    var routeMap by rememberSaveable { mutableStateOf(route.routeWebUrl ?: "") }

    StyledTextField(
        placeholder = "Route No",
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small2),
        onValueChange = { routeNo = it },
        inputText = routeNo,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
        selectedText = routeCategory.name,
        itemList = routeCategoryList.map {
            it.name
        }.toTypedArray(),
        onItemSelected = {
            routeCategory = routeCategoryList.first { category ->
                category.name == it
            }
        },
        fillMaxWidth = true
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
        placeholder = stringResource(R.string.route_details_entry),
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
        placeholder = stringResource(R.string.start_time_to_dsc_entry),
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
        placeholder = stringResource(R.string.departure_time_from_dsc_entry),
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
        placeholder = stringResource(R.string.route_map_google_maps_url),
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
        text = stringResource(R.string.submit)
    ) {
        if (
            routeNo.isEmpty() ||
            routeName.isEmpty() ||
            routeDetails.isEmpty() ||
            startTime.isEmpty() ||
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
                routeCategory = routeCategory,
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
                        "Route saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    result.data?.let {
                        navigator.navigate(
                            RouteDetailsScreenDestination(
                                route = it
                            )
                        ) {
                            popUpTo(RouteDetailsScreenDestination) {
                                inclusive = true
                            }
                        }
                    }
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
        StyledAlertDialog(
            title = stringResource(R.string.are_you_sure),
            message = stringResource(R.string.delete_route_confirmation),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirmButtonClick = {
                onCloseDialog()
                editRouteViewModel.deleteRoute(route.uuid)
            },
            onDismissButtonClick = {
                onCloseDialog()
            },
            onDismissRequest = {
                onCloseDialog()
            }
        )
    }
}