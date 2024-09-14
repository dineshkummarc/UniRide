package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarNoButton(
    title: String,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight? = FontWeight.Medium,
    fontFamily: FontFamily? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = fontFamily
            )
        },
        modifier = Modifier.shadow(elevation = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButton(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(LightGray)
                        .padding(8.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /* Do nothing */ },
                modifier = Modifier.padding(end = 12.dp)
            ) { }
        },
        modifier = Modifier.shadow(elevation = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButtonAndEndIcon(
    title: String,
    onBackClick: () -> Unit,
    endIcon: @Composable () -> Unit,
    endIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(LightGray)
                        .padding(8.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = { endIconClick() },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                endIcon()
            }
        },
        modifier = Modifier.shadow(elevation = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithNavDrawerIcon(
    title: String,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight? = FontWeight.Medium,
    fontFamily: FontFamily? = null,
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = fontFamily
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationIconClick() }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Do nothing */ }) { }
        },
        modifier = Modifier.shadow(elevation = 2.dp)
    )
}