package com.drdisagree.uniride.ui.components.navigation

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@NavGraph
annotation class MainScreenGraph(
    val start: Boolean = false
)

@RootNavGraph
@NavGraph
annotation class BottomNavGraph(
    val start: Boolean = false
)

@BottomNavGraph(start = true)
@NavGraph
annotation class HomeNavGraph(
    val start: Boolean = false
)

@BottomNavGraph
@NavGraph
annotation class RoutesNavGraph(
    val start: Boolean = false
)

@BottomNavGraph
@NavGraph
annotation class ScheduleNavGraph(
    val start: Boolean = false
)

@BottomNavGraph
@NavGraph
annotation class MoreNavGraph(
    val start: Boolean = false
)