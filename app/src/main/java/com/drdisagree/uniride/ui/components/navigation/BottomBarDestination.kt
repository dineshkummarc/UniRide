package com.drdisagree.uniride.ui.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.screens.NavGraph
import com.drdisagree.uniride.ui.screens.NavGraphs

enum class BottomBarDestination(
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val badgeCount: Int = 0,
    val graph: NavGraph
) {
    Home(
        label = R.string.nav_home,
        selectedIcon = R.drawable.ic_home_selected,
        unselectedIcon = R.drawable.ic_home_unselected,
        graph = NavGraphs.home
    ),
    Routes(
        label = R.string.nav_routes,
        selectedIcon = R.drawable.ic_route_selected,
        unselectedIcon = R.drawable.ic_route_unselected,
        graph = NavGraphs.routes
    ),
    Schedule(
        label = R.string.nav_schedule,
        selectedIcon = R.drawable.ic_schedule_selected,
        unselectedIcon = R.drawable.ic_schedule_unselected,
        graph = NavGraphs.schedule
    ),
    Settings(
        label = R.string.nav_more,
        selectedIcon = R.drawable.ic_more_selected,
        unselectedIcon = R.drawable.ic_more_unselected,
        graph = NavGraphs.more
    )
}