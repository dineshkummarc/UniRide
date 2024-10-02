package com.drdisagree.uniride.ui.screens.admin.more.panel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.destinations.NewBusCategoryDestination
import com.drdisagree.uniride.ui.screens.destinations.NewBusDestination
import com.drdisagree.uniride.ui.screens.destinations.NewLocationDestination
import com.drdisagree.uniride.ui.screens.destinations.NewNoticeDestination
import com.drdisagree.uniride.ui.screens.destinations.NewRouteCategoryDestination
import com.drdisagree.uniride.ui.screens.destinations.NewRouteDestination
import com.drdisagree.uniride.ui.screens.destinations.NewScheduleDestination
import com.drdisagree.uniride.ui.screens.destinations.PendingDriversDestination
import com.drdisagree.uniride.ui.screens.destinations.ReportedIssuesDestination
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.Gray15
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun AdminPanel(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.admin_panel_title),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                MoreContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun MoreContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    adminPanelViewModel: AdminPanelViewModel = hiltViewModel(),
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel()
) {
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()

    when (isAdminState) {
        null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(White)
                        .wrapContentSize()
                )
            }
        }

        true -> {
            val issueCount by adminPanelViewModel.issueCount.collectAsState()
            val pendingDriverCount by adminPanelViewModel.pendingDriverCount.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                PanelListItem(
                    icon = R.drawable.ic_document,
                    title = R.string.post_notice,
                    onClick = {
                        navigator.navigate(NewNoticeDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_bus_outline,
                    title = R.string.add_new_bus_name,
                    onClick = {
                        navigator.navigate(NewBusDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_new_map,
                    title = R.string.add_new_place,
                    onClick = {
                        navigator.navigate(NewLocationDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_new_category,
                    title = R.string.add_new_bus_category,
                    onClick = {
                        navigator.navigate(NewBusCategoryDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_new_route_category,
                    title = R.string.add_new_route_category,
                    onClick = {
                        navigator.navigate(NewRouteCategoryDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_routing,
                    title = R.string.add_new_bus_route,
                    onClick = {
                        navigator.navigate(NewRouteDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_new_schedule,
                    title = R.string.add_new_bus_schedule,
                    onClick = {
                        navigator.navigate(NewScheduleDestination)
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_user_list,
                    title = R.string.view_pending_driver_list,
                    onClick = {
                        navigator.navigate(PendingDriversDestination)
                    },
                    endIcon = {
                        when (pendingDriverCount) {
                            is Resource.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            is Resource.Error -> {
                                CountIndicator(count = -1)
                            }

                            is Resource.Success -> {
                                CountIndicator(
                                    count = (pendingDriverCount as Resource.Success<Int>).data ?: 0
                                )
                            }

                            else -> {}
                        }
                    }
                )
                PanelListItem(
                    icon = R.drawable.ic_warning_outline,
                    title = R.string.view_reported_issues,
                    onClick = {
                        navigator.navigate(ReportedIssuesDestination)
                    },
                    endIcon = {
                        when (issueCount) {
                            is Resource.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            is Resource.Error -> {
                                CountIndicator(count = -1)
                            }

                            is Resource.Success -> {
                                CountIndicator(
                                    count = (issueCount as Resource.Success<Int>).data ?: 0
                                )
                            }

                            else -> {}
                        }
                    }
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
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
private fun PanelListItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: (() -> Unit)? = null,
    endIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick?.invoke()
            }
            .padding(MaterialTheme.spacing.medium1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.medium2)
                .clip(RoundedCornerShape(MaterialTheme.spacing.small3))
                .background(Gray15)
                .padding(MaterialTheme.spacing.small3)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = stringResource(id = title),
            fontSize = 16.sp,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold
            )
        )

        if (endIcon != null) {
            Spacer(modifier = Modifier.weight(1f))
            endIcon()
        }
    }
}

@Composable
private fun CountIndicator(count: Int) {
    Box(
        modifier = Modifier
            .padding(start = MaterialTheme.spacing.small2)
            .clip(RoundedCornerShape(20.dp))
            .background(Blue)
            .padding(
                horizontal = MaterialTheme.spacing.small1,
                vertical = MaterialTheme.spacing.extraSmall1
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}