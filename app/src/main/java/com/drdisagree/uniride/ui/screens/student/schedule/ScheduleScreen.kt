package com.drdisagree.uniride.ui.screens.student.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.ScheduleNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.screens.destinations.ScheduleSearchScreenDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.random.Random

@ScheduleNavGraph(start = true)
@Destination(style = FadeInOutTransition::class)
@Composable
fun ScheduleScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(id = R.string.nav_schedule),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                                tint = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    },
                    endIconClick = {
                        navigator.navigate(
                            ScheduleSearchScreenDestination()
                        )
                    }
                )
            },
            content = { paddingValues ->
                ScheduleContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun ScheduleContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        repeat(15) { index ->
            item(key = index) {
                ScheduleListItem(
                    index = index,
                    routeNo = "${
                        remember {
                            listOf("Surjomukhi", "Dolpin", "Rojonigondha").random()
                        }
                    }-${Random.nextInt(1, 15)}",
                    routeName = "DSC to Dhanmondi"
                )
            }
        }
    }
}

@Composable
private fun ScheduleListItem(
    modifier: Modifier = Modifier,
    index: Int,
    routeNo: String,
    routeName: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (index != 0) {
            HorizontalDivider(
                color = LightGray,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium3,
                    vertical = MaterialTheme.spacing.medium1
                ),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = "Map with marker image",
                modifier = Modifier
                    .padding(end = 16.dp, top = 2.dp)
                    .size(28.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .padding(end = MaterialTheme.spacing.medium1),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = routeNo,
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                            append("From: ")
                        }
                        append("Dhanmondi (Female)")
                    },
                    color = Dark,
                    fontSize = 14.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                            append("To: ")
                        }
                        append("DSC")
                    },
                    color = Dark,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "07:00 AM",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = remember {
                        listOf("Common", "Employee", "Fixed", "Friday").random()
                    },
                    color = Dark,
                    fontSize = 14.sp
                )
            }
        }
    }
}