package com.drdisagree.uniride.ui.screens.student.schedule.search

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_CATEGORY
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_FROM
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_TO
import com.drdisagree.uniride.ui.components.navigation.ScheduleNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StyledDropDownMenu
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
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
private fun ScheduleSearchFields() {
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf(SCHEDULE_CATEGORY[0]) }
    var selectedPlaceFrom by remember { mutableStateOf(SCHEDULE_FROM[0]) }
    var selectedPlaceTo by remember { mutableStateOf(SCHEDULE_TO[0]) }

    StyledDropDownMenu(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.small2,
                end = MaterialTheme.spacing.small2
            ),
        selectedText = selectedCategory,
        itemList = SCHEDULE_CATEGORY.toTypedArray(),
        onItemSelected = {
            selectedCategory = it
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
        selectedText = selectedPlaceFrom,
        itemList = SCHEDULE_FROM.toTypedArray(),
        onItemSelected = {
            selectedPlaceFrom = it
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
        selectedText = selectedPlaceTo,
        itemList = SCHEDULE_TO.toTypedArray(),
        onItemSelected = {
            selectedPlaceTo = it
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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

    }
}