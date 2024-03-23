package com.drdisagree.uniride.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ButtonSecondary
import com.drdisagree.uniride.ui.extension.Container
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.screens.destinations.HomeContainerDestination
import com.drdisagree.uniride.ui.screens.destinations.InfoScreenDestination
import com.drdisagree.uniride.ui.screens.destinations.LoginScreenDestination
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@MainScreenGraph(start = true)
@Destination(style = SlideInOutTransition::class)
@Composable
fun OnBoardingScreen(
    navigator: DestinationsNavigator
) {
    val constraints = ConstraintSet {
        val studentButton = createRefFor("studentBtn")
        val driverButton = createRefFor("driverBtn")
        val guideline = createGuidelineFromStart(0.5f)
        val plantImage = createRefFor("plantImg")
        val personImage = createRefFor("personImg")
        val sunImage = createRefFor("sunImg")
        val titleText = createRefFor("titleText")
        val settingsIcon = createRefFor("settingsIcon")

        constrain(studentButton) {
            bottom.linkTo(driverButton.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
        }

        constrain(driverButton) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
        }

        constrain(plantImage) {
            bottom.linkTo(studentButton.top)
            end.linkTo(guideline)
        }

        constrain(personImage) {
            bottom.linkTo(studentButton.top)
            start.linkTo(guideline)
        }

        constrain(sunImage) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }

        constrain(titleText) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)

            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

        constrain(settingsIcon) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }
    }

    Container {
        OnBoardingScreenContent(
            navigator = navigator,
            constraints = constraints
        )
    }
}

@Composable
private fun OnBoardingScreenContent(
    navigator: DestinationsNavigator,
    constraints: ConstraintSet
) {
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.small3)
                .size(width = 58.dp, height = 100.dp)
                .layoutId("plantImg"),
            painter = painterResource(id = R.drawable.img_plant),
            contentDescription = "Picture of a plant"
        )

        Image(
            modifier = Modifier
                .padding(start = MaterialTheme.spacing.small3)
                .size(width = 115.dp, height = 315.dp)
                .layoutId("personImg"),
            painter = painterResource(id = R.drawable.img_person),
            contentDescription = "Picture of a person"
        )

        Image(
            modifier = Modifier
                .padding(start = 64.dp, top = 80.dp)
                .size(132.dp)
                .layoutId("sunImg"),
            painter = painterResource(id = R.drawable.img_sun),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Picture of the sun"
        )

        Text(
            modifier = Modifier
                .padding(start = MaterialTheme.spacing.medium3, top = 64.dp)
                .layoutId("titleText"),
            text = buildAnnotatedString {
                append("Track\nYour\n")

                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Destination")
                }
            },
            fontSize = 48.sp,
            lineHeight = 64.sp
        )

        Image(
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.medium3, top = MaterialTheme.spacing.medium3)
                .size(40.dp)
                .clip(CircleShape)
                .layoutId("settingsIcon")
                .clickable {
                    navigator.navigate(
                        InfoScreenDestination()
                    ) {
                        launchSingleTop = true
                    }
                }
                .padding(4.dp),
            painter = painterResource(id = R.drawable.ic_cog),
            contentScale = ContentScale.Fit,
            contentDescription = "Go to settings"
        )

        ButtonPrimary(
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.medium3,
                    end = MaterialTheme.spacing.medium3,
                    bottom = MaterialTheme.spacing.small2
                )
                .fillMaxWidth()
                .layoutId("studentBtn"),
            text = "I am a student",
            onClick = {
                navigator.navigate(
                    HomeContainerDestination()
                ) {
                    popUpTo(NavGraphs.root.startRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )

        ButtonSecondary(
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.medium3,
                    end = MaterialTheme.spacing.medium3,
                    bottom = MaterialTheme.spacing.medium3
                )
                .fillMaxWidth()
                .layoutId("driverBtn"),
            text = "I am a driver",
            onClick = {
                navigator.navigate(
                    LoginScreenDestination()
                ) {
                    launchSingleTop = true
                }
            }
        )
    }
}