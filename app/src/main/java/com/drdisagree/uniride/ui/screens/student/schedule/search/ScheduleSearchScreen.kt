package com.drdisagree.uniride.ui.screens.student.schedule.search

import android.text.format.DateFormat
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.ui.components.navigation.ScheduleNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.student.schedule.ScheduleListItem
import com.drdisagree.uniride.ui.screens.student.schedule.ScheduleViewModel
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.viewmodels.AccountStatusViewModel
import com.drdisagree.uniride.viewmodels.ListsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ScheduleNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun ScheduleSearchScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.nav_schedule),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                ScheduleSearchContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun ScheduleSearchContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScheduleSearchFieldsAndResult(navigator)
    }
}

@Composable
private fun ScheduleSearchFieldsAndResult(
    navigator: DestinationsNavigator,
    listsViewModel: ListsViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()
    val busCategoryList by listsViewModel.busCategoryModels.collectAsState()
    val placeList by listsViewModel.placeModels.collectAsState()
    val schedules by scheduleViewModel.allSchedules.collectAsState()
    val is24HourFormat = DateFormat.is24HourFormat(context)

    val defaultBusCategory = BusCategory(
        name = stringResource(R.string.category)
    )
    val defaultFrom = Place(
        name = stringResource(R.string.from)
    )
    val defaultTo = Place(
        name = stringResource(R.string.to)
    )

    var scheduleList by rememberSaveable { mutableStateOf(emptyList<Schedule>()) }
    var busCategory by rememberSaveable { mutableStateOf(defaultBusCategory) }
    var locationFrom by rememberSaveable { mutableStateOf(defaultFrom) }
    var locationTo by rememberSaveable { mutableStateOf(defaultTo) }
    var selectedBusCategory by rememberSaveable { mutableStateOf(defaultBusCategory) }
    var selectedLocationFrom by rememberSaveable { mutableStateOf(defaultFrom) }
    var selectedLocationTo by rememberSaveable { mutableStateOf(defaultTo) }

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                top = MaterialTheme.spacing.medium1,
                start = MaterialTheme.spacing.medium3,
                end = MaterialTheme.spacing.medium3
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
                start = MaterialTheme.spacing.medium3,
                end = MaterialTheme.spacing.medium3,
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
                start = MaterialTheme.spacing.medium3,
                end = MaterialTheme.spacing.medium3,
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

    ButtonPrimary(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium3,
                end = MaterialTheme.spacing.medium3,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium3
            )
            .fillMaxWidth(),
        text = stringResource(R.string.search)
    ) {
        var hasError = false

        if (busCategory.name == defaultBusCategory.name ||
            locationFrom.name == defaultFrom.name ||
            locationTo.name == defaultTo.name
        ) {
            Toast.makeText(
                context,
                "Please fill in all fields",
                Toast.LENGTH_SHORT
            ).show()

            hasError = true
        } else if (locationFrom == locationTo) {
            Toast.makeText(
                context,
                "Both locations cannot be the same",
                Toast.LENGTH_SHORT
            ).show()

            hasError = true
        }

        if (hasError) {
            selectedBusCategory = defaultBusCategory
            selectedLocationFrom = defaultFrom
            selectedLocationTo = defaultTo

            return@ButtonPrimary
        }

        selectedBusCategory = busCategory
        selectedLocationFrom = locationFrom
        selectedLocationTo = locationTo
    }

    when (schedules) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = MaterialTheme.spacing.large3,
                        bottom = MaterialTheme.spacing.medium1
                    ),
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

        is Resource.Success -> {
            scheduleList = schedules.data?.let { thisSchedules ->
                thisSchedules.filter { schedule ->
                    schedule.category == selectedBusCategory &&
                            schedule.from == selectedLocationFrom &&
                            schedule.to == selectedLocationTo
                }
            } ?: emptyList()

            if (scheduleList.isNotEmpty()) {
                repeat(scheduleList.size) { index ->
                    ScheduleListItem(
                        index = index,
                        schedule = scheduleList[index],
                        isAdmin = isAdminState ?: false,
                        navigator = navigator
                    )
                }
            } else if (selectedBusCategory.name != defaultBusCategory.name &&
                selectedLocationFrom.name != defaultFrom.name &&
                selectedLocationTo.name != defaultTo.name
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = MaterialTheme.spacing.large3,
                            bottom = MaterialTheme.spacing.medium1
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_schedules_found)
                    )
                }
            }
        }

        is Resource.Error -> {
            LaunchedEffect(schedules.message) {
                schedules.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        else -> {}
    }
}