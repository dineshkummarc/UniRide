package com.drdisagree.uniride.ui.components.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

private const val TIME_DURATION = 400

object FadeInOutTransition : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = TIME_DURATION)
        )
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = TIME_DURATION)
        )
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = TIME_DURATION)
        )
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = TIME_DURATION)
        )
    }
}