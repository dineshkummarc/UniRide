package com.drdisagree.uniride.utils

import androidx.compose.ui.graphics.Color
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Dark
import java.util.Locale

object ColorUtils {

    fun getIssuePillColors(isResolved: Boolean): Pair<Color, Color> {
        return if (isResolved) {
            Color(0xFFE9FAF4) to Color(0xFF0B710A)
        } else {
            Color(0xFFFCEFE7) to Color(0xFFE26A08)
        }
    }

    fun getBusIconColor(busName: String): Color {
        val busNameLowercase = busName.lowercase(Locale.getDefault())

        return if (busNameLowercase.contains("surjomukhi")) {
            Color(0xFFC67C3D)
        } else if (busNameLowercase.contains("dolphin")) {
            Color(0xFF25722F)
        } else if (busNameLowercase.contains("rojonigondha")) {
            Color(0xFF185190)
        } else { // unknown
            Black
        }
    }

    fun getSchedulePillColors(categoryName: String): Pair<Color, Color> {
        val categoryLowercase = categoryName.lowercase(Locale.getDefault())

        return if (categoryLowercase.contains("employee")) {
            Color(0xFFE9FAF4) to Color(0xFF0B710A)
        } else if (categoryLowercase.contains("fixed")) {
            Color(0xFFE7EFFC) to Color(0xFF085DE2)
        } else if (categoryLowercase.contains("friday")) {
            Color(0xFFFBEBEC) to Color(0xFF881418)
        } else { // common
            Color(0xFFF0F0F2) to Dark
        }
    }

    fun getRoutePillColors(routeName: String): Pair<Color, Color> {
        val categoryLowercase = routeName.lowercase(Locale.getDefault())

        return if (categoryLowercase.contains("shuttle")) {
            Color(0xFFE9FAF4) to Color(0xFF0B710A)
        } else if (categoryLowercase.contains("friday")) {
            Color(0xFFFFEEE6) to Color(0xFFAA6A48)
        } else { // common
            Color(0xFFF0F0F2) to Dark
        }
    }
}