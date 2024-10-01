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
fun PrivacyPolicyScreen(
    navigator: DestinationsNavigator
) {
    Container {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = "Privacy Policy",
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
            text = "UniRide (\"the App\") is committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use the App. By using the App, you agree to the collection and use of information in accordance with this policy. If you do not agree with the terms of this Privacy Policy, please do not use the App.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Information We Collect",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "Personal Information",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small1)
        )
        Text(
            text = "We may collect personal information that you voluntarily provide to us when you register for an account, use the App, or communicate with us. This information may include, but is not limited to:\n" +
                    "• Name\n" +
                    "• Email address\n" +
                    "• Profile picture",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Location Information",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App collects location information to provide live tracking of university vehicles. We may collect your real-time location data when you use location-based features of the App. This information is necessary to provide accurate and efficient service.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Usage Data",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We may collect information about your use of the App, such as your device type, operating system, IP address, access times, and the pages you view. This data helps us understand how the App is used and improve its functionality.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Cookies and Similar Technologies",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "The App may use cookies and similar technologies to enhance your experience. Cookies are small data files stored on your device that help us recognize you and understand your preferences. You can control the use of cookies through your device settings.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "How We Use Your Information",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We use the information we collect for various purposes, including:\n" +
                    "• To provide, operate, and maintain the App\n" +
                    "• To improve and personalize your experience\n" +
                    "• To process transactions and manage your account\n" +
                    "• To communicate with you, including sending updates and notifications\n" +
                    "• To monitor and analyze usage and trends to improve the App\n" +
                    "• To protect the security and integrity of the App\n" +
                    "• To comply with legal obligations",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Information Sharing and Disclosure",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We may share your information with third parties in the following circumstances:\n" +
                    "• With your consent\n" +
                    "• With service providers who perform services on our behalf\n" +
                    "• To comply with legal obligations, such as responding to a subpoena or court order\n" +
                    "• To protect and defend our rights and property\n" +
                    "• In connection with a business transaction, such as a merger or acquisition\n" +
                    "We do not sell or rent your personal information to third parties.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Data Security",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We take reasonable measures to protect your information from unauthorized access, use, or disclosure. However, no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee the absolute security of your information.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Retention of Information",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We will retain your personal information only for as long as necessary to fulfill the purposes for which it was collected, including legal, accounting, or reporting requirements.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Your Rights and Choices",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "You have the following rights regarding your personal information:\n" +
                    "• Access: You can request access to the personal information we hold about you.\n" +
                    "• Correction: You can request that we correct any inaccurate or incomplete information.\n" +
                    "• Deletion: You can request that we delete your personal information, subject to certain legal restrictions.\n" +
                    "• Objection: You can object to the processing of your personal information in certain circumstances.\n" +
                    "• Withdrawal of Consent: If you have provided consent for the collection and processing of your personal information, you can withdraw it at any time.\n" +
                    "To exercise any of these rights, please contact us at mhofficial2020@gmail.com.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Changes to This Privacy Policy",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on the App. Your continued use of the App after the changes are posted constitutes your acceptance of the revised policy.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "Contact Us",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
        Text(
            text = "If you have any questions or concerns about this Privacy Policy or our data practices, please contact us at:\n" +
                    "Daffodil International University\n" +
                    "Daffodil Smart City, Birulia, Savar, Dhaka, Bangladesh\n" +
                    "mhofficial2020@gmail.com\n" +
                    "+8801880890777",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Text(
            text = "By using the App, you acknowledge that you have read, understood, and agree to be bound by this Privacy Policy.",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
        )
    }
}