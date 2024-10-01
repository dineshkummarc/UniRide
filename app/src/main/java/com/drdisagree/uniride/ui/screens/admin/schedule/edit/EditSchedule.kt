package com.drdisagree.uniride.ui.screens.admin.schedule.edit

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.ui.components.navigation.ScheduleNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TimePickerDialog
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.drdisagree.uniride.utils.viewmodels.ListsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@ScheduleNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun EditSchedule(
    navigator: DestinationsNavigator,
    schedule: Schedule,
    editScheduleViewModel: EditScheduleViewModel = hiltViewModel()
) {
    var openDialog by remember { mutableStateOf(false) }

    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(R.string.edit_schedule),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = "Delete",
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
                EditScheduleContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    schedule = schedule,
                    editScheduleViewModel = editScheduleViewModel,
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
private fun EditScheduleContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    schedule: Schedule,
    editScheduleViewModel: EditScheduleViewModel,
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
                EditScheduleFields(
                    schedule = schedule,
                    navigator = navigator,
                    editScheduleViewModel = editScheduleViewModel,
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
private fun EditScheduleFields(
    schedule: Schedule,
    navigator: DestinationsNavigator,
    editScheduleViewModel: EditScheduleViewModel,
    openDialog: Boolean,
    onCloseDialog: () -> Unit,
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val busList by listsViewModel.busModels.collectAsState()
    val busCategoryList by listsViewModel.busCategoryModels.collectAsState()
    val placeList by listsViewModel.placeModels.collectAsState()

    var selectedBus by remember { mutableStateOf(schedule.bus) }
    var busCategory by rememberSaveable { mutableStateOf(schedule.category) }
    var locationFrom by rememberSaveable { mutableStateOf(schedule.from) }
    var locationTo by rememberSaveable { mutableStateOf(schedule.to) }
    var departureTime by rememberSaveable { mutableStateOf(schedule.time) }
    var showTimePicker by remember { mutableStateOf(false) }
    val is24HourFormat = DateFormat.is24HourFormat(context)
    var departureTimeInMillis by rememberSaveable {
        mutableLongStateOf(
            timeToMillisConverter(
                departureTime,
                is24HourFormat
            )
        )
    }

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
            modifier = Modifier.padding(horizontal = 22.dp),
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
        if (locationFrom == locationTo) {
            Toast.makeText(
                context,
                "Both locations cannot be the same",
                Toast.LENGTH_SHORT
            ).show()

            return@ButtonPrimary
        }

        editScheduleViewModel.editSchedule(
            schedule.copy(
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
        editScheduleViewModel.editState.collect { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingDialog = true
                }

                is Resource.Success -> {
                    showLoadingDialog = false

                    Toast.makeText(
                        context,
                        "Schedule saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    result.data?.let {
                        navigator.navigateUp()
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

    LaunchedEffect(Unit) {
        editScheduleViewModel.deleteState.collect { result ->
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

                    navigator.navigateUp()
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
            title = "Are you sure?",
            message = "This action cannot be undone. Delete this schedule?",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            onConfirmButtonClick = {
                onCloseDialog()
                editScheduleViewModel.deleteSchedule(schedule.uuid)
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

private fun timeToMillisConverter(scheduleTime: String, is24HourFormat: Boolean): Long {
    val sdf = SimpleDateFormat(if (is24HourFormat) "HH:mm" else "hh:mm a", Locale.getDefault())
    val date: Date? = sdf.parse(scheduleTime)
    val millis: Long = date?.time ?: 0

    return millis
}