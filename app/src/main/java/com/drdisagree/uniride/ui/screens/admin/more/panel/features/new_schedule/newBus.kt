package com.drdisagree.uniride.ui.screens.admin.more.panel.features.new_schedule

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.drdisagree.uniride.data.models.Bus
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.admin.account.AccountStatusViewModel
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun NewSchedule(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(R.string.new_schedule),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                NewScheduleContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun NewScheduleContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
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
                NewScheduleFields()
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
private fun NewScheduleFields(
    newScheduleViewModel: NewScheduleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedBus by remember { mutableStateOf("Select Bus") }
    var busCategory by rememberSaveable { mutableStateOf("Common") }
    var locationFrom by rememberSaveable { mutableStateOf("") }
    var locationTo by rememberSaveable { mutableStateOf("") }
    var departureTime by rememberSaveable { mutableStateOf("") }

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2
            ),
        selectedText = selectedBus,
        itemList = arrayOf("Select Bus", "Surjomukhi", "Dolphin", "Rojonigondha"),
        onItemSelected = {
            selectedBus = it
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        },
        fillMaxWidth = true
    )

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
        selectedText = busCategory,
        itemList = arrayOf("Common", "Employee"),
        onItemSelected = {
            busCategory = it
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        },
        fillMaxWidth = true
    )

    StyledTextField(
        placeholder = "From",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { locationFrom = it },
        inputText = locationFrom,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false
    )

    StyledTextField(
        placeholder = "To",
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.small2,
            end = MaterialTheme.spacing.small2,
            top = MaterialTheme.spacing.medium1
        ),
        onValueChange = { locationTo = it },
        inputText = locationTo,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    StyledTextField(
        placeholder = "Time",
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
            selectedBus.isEmpty() ||
            busCategory.isEmpty() ||
            locationFrom.isEmpty() ||
            locationTo.isEmpty() ||
            departureTime.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Please fill in all fields",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        newScheduleViewModel.saveSchedule(
            Schedule(
                bus = Bus(
                    name = selectedBus
                ),
                category = busCategory,
                from = locationFrom,
                to = locationTo,
                time = departureTime
            )
        )
    }

    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        newScheduleViewModel.state.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    selectedBus = ""
                    busCategory = ""
                    locationFrom = ""
                    locationTo = ""
                    departureTime = ""

                    Toast.makeText(
                        context,
                        result.data,
                        Toast.LENGTH_SHORT
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