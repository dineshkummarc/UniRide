package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    rating: Int,
    starSize: Float = 12f,
    starSpacing: Float = 0.5f,
    onRatingChanged: ((Int) -> Unit)? = null
) {
    val density = LocalDensity.current.density
    val starSizes = (starSize * density).dp
    val starSpacings = (starSpacing * density).dp

    Row(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20000000)

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .then(
                        if (onRatingChanged != null) {
                            Modifier
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        onRatingChanged(i)
                                    }
                                )
                        } else {
                            Modifier
                        }
                    )
                    .size(starSizes)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacings))
            }
        }
    }
}