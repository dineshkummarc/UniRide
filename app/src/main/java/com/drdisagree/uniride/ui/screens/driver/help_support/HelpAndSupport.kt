package com.drdisagree.uniride.ui.screens.driver.help_support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.utils.Constant.EMERGENCY_PHONE_NUMBERS
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MainScreenGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun HelpAndSupportScreen(
    navigator: DestinationsNavigator
) {
    Container {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(R.string.help_support),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                InfoContent(paddingValues)
            }
        )
    }
}

@Composable
private fun InfoContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium1)
    ) {
        Text(
            text = "Welcome to UniRide Support!",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "We're here to help you get the most out of your UniRide experience. Below you'll find useful information and resources.",
            fontSize = 14.sp
        )
        Text(
            text = "Navigating the App",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "Home:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = "• View your upcoming schedules and current assignments.\n" +
                    "• Get real-time updates on your rides.",
            fontSize = 14.sp
        )
        Text(
            text = "Profile:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "• Update your personal information.\n" +
                    "• View your driving history and performance.",
            fontSize = 14.sp
        )
        Text(
            text = "Common Issues",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "App Crashes or Freezes:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = "• Restart the app and try again.\n" +
                    "• Ensure you have the latest version installed.\n" +
                    "• Clear the app cache from your device settings.",
            fontSize = 14.sp
        )
        Text(
            text = "GPS Issues:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "• Ensure your device's location services are enabled.\n" +
                    "• Restart your device if the problem persists.",
            fontSize = 14.sp
        )
        Text(
            text = "Contact Support",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "If you need further assistance, don't hesitate to reach out:\n" +
                    "• Email: mhofficial2020@gmail.com\n" +
                    "• Phone: +8801880890777",
            fontSize = 14.sp
        )
        Text(
            text = "FAQs",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "How do I update my route information?",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = "• Go to the home screen and select the route you need to update. Make changes and save.",
            fontSize = 14.sp
        )
        Text(
            text = "What should I do if I encounter an emergency?",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "• Contact the emergency hotline at ${EMERGENCY_PHONE_NUMBERS.first().first}.",
            fontSize = 14.sp
        )
        Text(
            text = "How do I report an issue with a ride?",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "• Contact us at mhofficial2020@gmail with screenshots and details.",
            fontSize = 14.sp
        )
        Text(
            text = "Tips for a Smooth Ride",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "• Stay Updated: Regularly check for app updates to ensure you have the latest features and bug fixes.\n" +
                    "• Stay Connected: Keep your device charged and maintain a stable internet connection.\n" +
                    "• Stay Safe: Follow all traffic laws and drive responsibly.",
            fontSize = 14.sp
        )
        Text(
            text = "Thank you for being a part of UniRide. Safe travels!",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
    }
}