package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun PlantBottomCentered(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            modifier = modifier
                .padding(end = MaterialTheme.spacing.small3)
                .size(width = 58.dp, height = 100.dp)
                .align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.img_plant),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Gray)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlantBottomCenteredPreview() {
    PlantBottomCentered()
}