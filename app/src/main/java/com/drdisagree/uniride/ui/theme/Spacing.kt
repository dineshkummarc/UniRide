package com.drdisagree.uniride.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall1: Dp = 0.dp,
    val extraSmall2: Dp = 0.dp,
    val small1: Dp = 0.dp,
    val small2: Dp = 0.dp,
    val small3: Dp = 0.dp,
    val medium1: Dp = 0.dp,
    val medium2: Dp = 0.dp,
    val medium3: Dp = 0.dp,
    val large1: Dp = 0.dp,
    val large2: Dp = 0.dp,
    val large3: Dp = 0.dp,
    val extraLarge1: Dp = 0.dp,
    val extraLarge2: Dp = 0.dp,
    val fieldHeight: Dp = 64.dp
)

val MediumSpacing = Spacing(
    extraSmall1 = 2.dp,
    extraSmall2 = 4.dp,
    small1 = 6.dp,
    small2 = 8.dp,
    small3 = 12.dp,
    medium1 = 16.dp,
    medium2 = 20.dp,
    medium3 = 24.dp,
    large1 = 30.dp,
    large2 = 36.dp,
    large3 = 42.dp,
    extraLarge1 = 48.dp,
    extraLarge2 = 60.dp,
    fieldHeight = 64.dp
)