package com.drdisagree.uniride.ui.screens.route

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.ui.components.navigation.RoutesNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.extension.Container
import com.drdisagree.uniride.ui.screens.destinations.RouteDetailsScreenDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RoutesNavGraph(start = true)
@Destination(style = FadeInOutTransition::class)
@Composable
fun RouteScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.nav_routes),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                RouteContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun RouteContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues
) {
    val route = remember {
        Route(
            routeNo = "R1",
            routeName = "Dhanmondi <> DSC",
            routeDetails = "Dhanmondi - Sobhanbag <> Shyamoli Square <> Technical Mor <> Majar Road Gabtoli <> Konabari Bus Stop <> Eastern Housing <> Rupnagar <> Birulia Bus Stand <> Daffodil Smart City",
            startTime = "7:20 AM, 10:00 AM, 2:00 PM",
            departureTime = "1:00 PM (Only for Students bus), 3:20 PM, 4:10 PM",
            routeWebUrl = "https://www.google.com/maps/d/embed?mid=1J8QtXb3iMgXJTsECsIzdzu3mIgDio5Al"
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        repeat(15) { index ->
            item(
                key = index
            ) {
                RoutesListItem(
                    index = index,
                    routeNo = "Route ${index + 1}",
                    routeName = "Dhanmondi <> DSC",
                    onClick = {
                        navigator.navigate(
                            RouteDetailsScreenDestination(route)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RoutesListItem(
    modifier: Modifier = Modifier,
    index: Int,
    routeNo: String,
    routeName: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick?.invoke()
            }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.align(Alignment.Top)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_map_with_marker),
                    contentDescription = "Map with marker image",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.medium1),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = routeNo,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = routeName,
                    color = Dark,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "View details",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}