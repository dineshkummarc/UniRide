package com.drdisagree.uniride.ui.screens.student.route

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.data.models.RouteCategory
import com.drdisagree.uniride.ui.components.navigation.RoutesNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.destinations.RouteDetailsScreenDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Locale

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
    paddingValues: PaddingValues,
    routeViewModel: RouteViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        var showLoadingDialog by rememberSaveable { mutableStateOf(false) }
        val routes by routeViewModel.routes.collectAsState(initial = Resource.Unspecified())
        var routeCategories by rememberSaveable { mutableStateOf<List<RouteCategory>>(emptyList()) }
        var selectedCategories by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

        LaunchedEffect(routes) {
            if (routes is Resource.Success) {
                val routeData = (routes as Resource.Success<List<Route>>).data

                val uniqueCategories = routeData
                    ?.groupBy { it.routeCategory }
                    ?.map { (category, routes) ->
                        category to routes.minOfOrNull { it.timeStamp }
                    }
                    ?.filter { it.second != null }
                    ?.sortedBy { it.second }
                    ?.map { it.first }

                if (uniqueCategories != null) {
                    routeCategories = uniqueCategories
                }
            }
        }

        val filteredRoutes = if (selectedCategories.isEmpty()) {
            (routes as? Resource.Success<List<Route>>)?.data
                ?.sortedBy { it.timeStamp }
                ?: emptyList()
        } else {
            (routes as? Resource.Success<List<Route>>)?.data
                ?.filter { it.routeCategory.name in selectedCategories }
                ?.sortedBy { it.timeStamp }
                ?: emptyList()
        }

        when (routes) {
            is Resource.Loading -> {
                showLoadingDialog = true
            }

            is Resource.Success -> {
                showLoadingDialog = false

                if (filteredRoutes.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = paddingValues)
                    ) {
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    horizontal = MaterialTheme.spacing.medium1,
                                    vertical = MaterialTheme.spacing.medium1
                                ),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small2)
                            ) {
                                items(
                                    count = routeCategories.size,
                                    key = { routeCategories[it].uuid }
                                ) { index ->
                                    val category = routeCategories[index]
                                    val isSelected = category.name in selectedCategories
                                    val (categoryPillBackgroundColor, categoryPillTextColor) = getCategoryColors(
                                        category.name
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(28.dp))
                                            .background(categoryPillBackgroundColor)
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) categoryPillTextColor else Color.Transparent,
                                                shape = RoundedCornerShape(28.dp)
                                            )
                                            .clickable {
                                                selectedCategories = if (isSelected) {
                                                    selectedCategories - category.name
                                                } else {
                                                    selectedCategories + category.name
                                                }
                                            }
                                            .padding(
                                                horizontal = MaterialTheme.spacing.medium1,
                                                vertical = MaterialTheme.spacing.small1
                                            )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = category.name,
                                                color = categoryPillTextColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium
                                            )

                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = null,
                                                    tint = categoryPillTextColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        itemsIndexed(
                            items = filteredRoutes,
                            key = { _, route -> route.uuid }
                        ) { index, route ->
                            RoutesListItem(
                                index = index,
                                route = route,
                                onClick = {
                                    navigator.navigate(
                                        RouteDetailsScreenDestination(route)
                                    )
                                }
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No routes available for the selected categories!"
                        )
                    }
                }
            }

            is Resource.Error -> {
                showLoadingDialog = false

                Toast.makeText(
                    context,
                    (routes as Resource.Error<List<Route>>).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Unit
            }
        }

        if (showLoadingDialog) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun RoutesListItem(
    modifier: Modifier = Modifier,
    index: Int,
    route: Route,
    onClick: (() -> Unit)? = null
) {
    val (categoryPillBackgroundColor, categoryPillTextColor) = getCategoryColors(
        route.routeCategory.name
    )

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
                .height(IntrinsicSize.Min)
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Route ${route.routeNo}",
                        fontSize = 16.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.small1)
                            .clip(RoundedCornerShape(28.dp))
                            .background(categoryPillBackgroundColor)
                            .padding(
                                horizontal = MaterialTheme.spacing.small2,
                                vertical = MaterialTheme.spacing.extraSmall1
                            )
                    ) {
                        Text(
                            text = route.routeCategory.name,
                            color = categoryPillTextColor,
                            fontSize = 14.sp
                        )
                    }
                }
                Text(
                    text = route.routeName,
                    color = Dark,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "View details",
                tint = Color.Black,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LightGray)
                    .padding(8.dp)
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun getCategoryColors(categoryName: String): Pair<Color, Color> {
    val categoryLowercase = categoryName.lowercase(Locale.getDefault())
    val backgroundColor = when {
        categoryLowercase.contains("shuttle") -> Color(0xFFE9FAF4)
        categoryLowercase.contains("friday") -> Color(0xFFFFEEE6)
        else -> Color(0xFFF0F0F2)
    }
    val textColor = when {
        categoryLowercase.contains("shuttle") -> Color(0xFF0B710A)
        categoryLowercase.contains("friday") -> Color(0xFFAA6A48)
        else -> Dark
    }
    return backgroundColor to textColor
}