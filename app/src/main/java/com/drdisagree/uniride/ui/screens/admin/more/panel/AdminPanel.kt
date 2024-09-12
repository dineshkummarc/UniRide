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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.drdisagree.uniride.ui.screens.destinations.NewBusCategoryDestination
import com.drdisagree.uniride.ui.screens.destinations.NewBusDestination
import com.drdisagree.uniride.ui.screens.destinations.NewLocationDestination
import com.drdisagree.uniride.ui.screens.destinations.NewNoticeDestination
import com.drdisagree.uniride.ui.screens.destinations.NewRouteDestination
import com.drdisagree.uniride.ui.screens.destinations.NewScheduleDestination
import com.drdisagree.uniride.ui.theme.Gray15
import com.drdisagree.uniride.ui.theme.spacing
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
                modifier = Modifier.fillMaxSize(),
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
                    icon = R.drawable.ic_category,
                    title = R.string.add_new_bus_category,
                    onClick = {
                        navigator.navigate(NewBusCategoryDestination)
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
                    title = R.string.view_driver_list
                )
                PanelListItem(
                    icon = R.drawable.ic_warning,
                    title = R.string.view_emergency_situations
                )
                PanelListItem(
                    icon = R.drawable.ic_sms_edit,
                    title = R.string.view_reported_issues
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
                    text = "You are not an admin"
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
    onClick: (() -> Unit)? = null
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
                fontWeight = FontWeight.Bold
            )
        )
    }
}