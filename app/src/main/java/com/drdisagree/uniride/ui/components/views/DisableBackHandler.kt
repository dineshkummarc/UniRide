package com.drdisagree.uniride.ui.components.views

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun DisableBackHandler(
    isDisabled: Boolean,
    content: @Composable () -> Unit
) {
    if (isDisabled) {
        // This will consume the back press event
        BackHandler {
            // Do nothing to disable the back navigation
        }
    }

    content()
}
