package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeHeader(
    driverScreen: Boolean = false,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.header_background),
            contentDescription = "Header background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent
                        )
                    )
                )
        )
        Image(
            painter = painterResource(
                if (driverScreen) {
                    R.drawable.ic_driver_navigator
                } else {
                    R.drawable.ic_my_current_location
                }
            ),
            contentDescription = "Vector image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = if (driverScreen) 40.dp else 27.dp,
                    end = 6.dp
                )
                .width(200.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (driverScreen) {
                Image(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                    colorFilter = ColorFilter.tint(Black),
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = MaterialTheme.spacing.medium1
                        )
                        .size(28.dp)
                        .clickable {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.ic_launcher_icon),
                    contentDescription = "App logo",
                    colorFilter = ColorFilter.tint(Black),
                    modifier = Modifier
                        .padding(
                            bottom = 24.dp,
                            start = MaterialTheme.spacing.medium1
                        )
                        .size(32.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                fontSize = 28.sp,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium1
                )
            )
            Text(
                text = "Ready to ${if (driverScreen) "drive" else "explore"}?",
                fontSize = 20.sp,
                letterSpacing = 0.6.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium1
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomSheetStyleHeader()
        }
    }
}