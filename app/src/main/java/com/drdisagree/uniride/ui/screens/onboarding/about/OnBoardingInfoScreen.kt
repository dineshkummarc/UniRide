package com.drdisagree.uniride.ui.screens.onboarding.about

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.BuildConfig
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.destinations.PrivacyPolicyScreenDestination
import com.drdisagree.uniride.ui.screens.destinations.TermsAndConditionsScreenDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.copyToClipboard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MainScreenGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun InfoScreen(
    navigator: DestinationsNavigator
) {
    Container {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = "Settings",
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                InfoContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun InfoContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.medium3)
                .size(width = 60.dp, height = 60.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.ic_launcher_icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black)
        )

        Text(
            text = stringResource(id = R.string.app_name).uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.small2)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier
                .padding(
                    top = MaterialTheme.spacing.medium1,
                    bottom = MaterialTheme.spacing.small3,
                    start = MaterialTheme.spacing.medium1,
                    end = MaterialTheme.spacing.medium1
                )
                .height(1.dp)
                .fillMaxWidth()
                .background(color = Gray)
        )

        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("Version\n")
            }
            withStyle(
                style = SpanStyle(
                    color = DarkGray,
                    fontSize = 13.sp
                )
            ) {
                append(BuildConfig.VERSION_NAME)
            }
        }

        val context = LocalContext.current
        val appName = context.getString(R.string.app_name)
        val textToCopy by remember { mutableStateOf("$appName ${BuildConfig.VERSION_NAME}") }

        Text(
            text = annotatedString,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    copyToClipboard(context, textToCopy, "Version")
                    Toast
                        .makeText(
                            context,
                            "Copied to Clipboard",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .padding(
                    vertical = MaterialTheme.spacing.small3,
                    horizontal = MaterialTheme.spacing.medium3
                )
        )

        Text(
            text = "Terms and Conditions",
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        TermsAndConditionsScreenDestination
                    ) {
                        launchSingleTop = true
                    }
                }
                .padding(
                    vertical = MaterialTheme.spacing.small3,
                    horizontal = MaterialTheme.spacing.medium3
                )
        )

        Text(
            text = "Privacy Policy",
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(
                        PrivacyPolicyScreenDestination
                    ) {
                        launchSingleTop = true
                    }
                }
                .padding(
                    vertical = MaterialTheme.spacing.small3,
                    horizontal = MaterialTheme.spacing.medium3
                )
        )

        Text(
            text = "MADE BY MAHMUD ❤".uppercase(),
            fontSize = 11.sp,
            color = Dark,
            letterSpacing = TextUnit(2f, TextUnitType.Sp),
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.extraLarge2)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}