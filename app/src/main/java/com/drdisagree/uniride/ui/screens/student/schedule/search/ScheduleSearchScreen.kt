package com.drdisagree.uniride.ui.screens.student.schedule.search

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.ui.components.navigation.ScheduleNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.global.viewmodels.ListsViewModel
import com.drdisagree.uniride.ui.theme.spacing
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .padding(MaterialTheme.spacing.medium1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = -1) {
            ScheduleSearchFields()
        }
    }
}

@Composable
private fun ScheduleSearchFields(
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val busCategoryList by listsViewModel.busCategoryModels.collectAsState()
    val placeList by listsViewModel.placeModels.collectAsState()

    val defaultBusCategory = BusCategory(
        name = "Category"
    )
    val defaultFrom = Place(
        name = "From"
    )
    val defaultTo = Place(
        name = "To"
    )

    val context = LocalContext.current
    var busCategory by rememberSaveable { mutableStateOf(defaultBusCategory) }
    var locationFrom by rememberSaveable { mutableStateOf(defaultFrom) }
    var locationTo by rememberSaveable { mutableStateOf(defaultTo) }

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2
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

    ButtonPrimary(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth(),
        text = "Search"
    ) {
        if (busCategory.name == defaultBusCategory.name ||
            locationFrom.name == defaultFrom.name ||
            locationTo.name == defaultTo.name
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

        // TODO: Implement search
    }
}