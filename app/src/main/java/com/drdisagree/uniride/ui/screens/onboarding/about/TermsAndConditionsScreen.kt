package com.drdisagree.uniride.ui.screens.onboarding.about

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
fun TermsAndConditionsScreen(
    navigator: DestinationsNavigator
) {
    Container {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = "Terms and Conditions",
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
            .padding(
                start = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
    ) {
        Text(
            text = "Introduction",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Welcome to UniRide! These Terms and Conditions govern your use of our mobile application (\"the App\"), which allows students to check the live location of university vehicles, view routes and schedules, and access driver reviews. By using the App, you agree to comply with and be bound by these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Eligibility",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App is intended for use by students of Daffodil International University. By using the App, you confirm that you are a current student of Daffodil International University and have the necessary permissions to use this service.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Use of the App",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "You agree to use the App only for its intended purposes and in accordance with these Terms and Conditions. You must not use the App for any unlawful or prohibited activities.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Live Location Tracking",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App provides live location tracking of university vehicles. You acknowledge that the accuracy of this information may vary and is dependent on the GPS signal and other technical factors.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Routes and Schedules",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App provides information on vehicle routes and schedules. This information is subject to change, and while we strive to keep it up-to-date, we cannot guarantee its accuracy at all times.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Driver Reviews",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App allows students to view and submit reviews of drivers. All reviews must be honest and respectful. We reserve the right to remove any reviews that are deemed inappropriate or offensive.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Privacy and Data Collection",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "Your privacy is important to us. Please review our Privacy Policy to understand how we collect, use, and protect your personal information.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "User Conduct",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "You agree not to:\n" +
                    "• Use the App in any manner that could damage, disable, overburden, or impair the App.\n" +
                    "• Use any automated system or software to extract data from the App for commercial purposes.\n" +
                    "• Attempt to gain unauthorized access to the App, its servers, or any data therein.\n" +
                    "• Harass, abuse, or harm other users of the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Intellectual Property",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "All content and materials on the App, including but not limited to text, graphics, logos, and software, are the property of Daffodil International University or its licensors and are protected by intellectual property laws. You may not use, reproduce, or distribute any content from the App without prior written permission from Daffodil International University.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Disclaimers",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App is provided on an \"as is\" and \"as available\" basis. We make no warranties or representations, express or implied, regarding the operation or availability of the App or the information, content, or materials included on the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Limitation of Liability",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "To the fullest extent permitted by law, Daffodil International University shall not be liable for any damages of any kind arising from the use of the App or from any information, content, or materials included on the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Changes to Terms and Conditions",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We reserve the right to modify these Terms and Conditions at any time. Any changes will be effective immediately upon posting the revised Terms and Conditions on the App. Your continued use of the App following the posting of changes constitutes your acceptance of those changes.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Termination",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We reserve the right to terminate or suspend your access to the App at any time, without notice, for conduct that we believe violates these Terms and Conditions or is harmful to other users of the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Governing Law",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "These Terms and Conditions shall be governed by and construed in accordance with the laws of Bangladesh, without regard to its conflict of laws principles.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Contact Information",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "If you have any questions or concerns about these Terms and Conditions, please contact us at mhofficial2020@gmail.com.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "By using the App, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
    }
}