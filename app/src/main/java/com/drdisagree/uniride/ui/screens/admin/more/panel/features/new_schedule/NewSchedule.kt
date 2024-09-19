package com.drdisagree.uniride.ui.screens.admin.more.panel.features.new_schedule

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Bus
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TimePickerDialog
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.drdisagree.uniride.utils.viewmodels.ListsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.text.SimpleDateFormat
import java.util.Locale

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
    newScheduleViewModel: NewScheduleViewModel = hiltViewModel(),
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val busList by listsViewModel.busModels.collectAsState()
    val busCategoryList by listsViewModel.busCategoryModels.collectAsState()
    val placeList by listsViewModel.placeModels.collectAsState()

    val defaultBusName = Bus(
        name = "Bus Name"
    )
    val defaultBusCategory = BusCategory(
        name = "Category"
    )
    val defaultFrom = Place(
        name = "From"
    )
    val defaultTo = Place(
        name = "To"
    )
    val defaultTime = "Time"

    val context = LocalContext.current
    var selectedBus by remember { mutableStateOf(defaultBusName) }
    var busCategory by rememberSaveable { mutableStateOf(defaultBusCategory) }
    var locationFrom by rememberSaveable { mutableStateOf(defaultFrom) }
    var locationTo by rememberSaveable { mutableStateOf(defaultTo) }
    var departureTime by rememberSaveable { mutableStateOf(defaultTime) }
    var departureTimeInMillis by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var showTimePicker by remember { mutableStateOf(false) }
    val is24HourFormat = DateFormat.is24HourFormat(context)

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2
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

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
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

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
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

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            ),
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

    Box(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium1))
            .background(color = Color.White.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = Gray,
                shape = RoundedCornerShape(MaterialTheme.spacing.medium1)
            )
            .clickable {
                showTimePicker = true
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp),
            text = departureTime
        )
    }

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
        if (selectedBus.name == defaultBusName.name ||
            busCategory.name == defaultBusCategory.name ||
            locationFrom.name == defaultFrom.name ||
            locationTo.name == defaultTo.name ||
            departureTime == defaultTime
        ) {
            Toast.makeText(
                context,
                "Please fill in all fields",
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

        newScheduleViewModel.saveSchedule(
            Schedule(
                bus = selectedBus,
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

                    selectedBus = defaultBusName
                    busCategory = defaultBusCategory
                    locationFrom = defaultFrom
                    locationTo = defaultTo
                    departureTime = defaultTime

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

    if (showTimePicker) {
        TimePickerDialog(
            onConfirm = {
                departureTime = SimpleDateFormat(
                    if (is24HourFormat) "HH:mm" else "hh:mm a",
                    Locale.getDefault()
                ).format(it.time)
                departureTimeInMillis = it.timeInMillis

                showTimePicker = false
            },
            onCancel = { showTimePicker = false },
            selectedTime = departureTimeInMillis
        )
    }

    if (showLoadingDialog) {
        LoadingDialog()
    }
}