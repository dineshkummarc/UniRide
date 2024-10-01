package com.drdisagree.uniride.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {

    fun getIssuePillColors(isResolved: Boolean): Pair<Color, Color> {
        return if (isResolved) {
            Color(0xFFE9FAF4) to Color(0xFF0B710A)
        } else {
            Color(0xFFFCEFE7) to Color(0xFFE26A08)
        }
    }
}