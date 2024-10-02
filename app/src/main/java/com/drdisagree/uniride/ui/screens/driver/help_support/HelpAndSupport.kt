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
            text = stringResource(R.string.uniride_support_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.uniride_support_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.navigating_the_app),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.navigating_app_home_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = stringResource(R.string.navigating_app_home_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.navigating_app_profile_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.navigating_app_profile_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.common_issues_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.app_crashes_or_freezes_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = stringResource(R.string.app_crashes_or_freezes_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.gps_issues_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.gps_issues_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.contact_support_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.contact_support_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.faqs_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.update_tracking_information_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = stringResource(R.string.update_tracking_information_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.encounter_emergency_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(
                R.string.encounter_emergency_summary,
                EMERGENCY_PHONE_NUMBERS.first().first
            ),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.report_issue_about_uniride_title),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.report_issue_about_uniride_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.tips_for_smooth_ride_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = stringResource(R.string.tips_for_smooth_ride_summary),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.part_of_uniride),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
    }
}