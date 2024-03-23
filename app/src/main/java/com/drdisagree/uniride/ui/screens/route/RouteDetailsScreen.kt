package com.drdisagree.uniride.ui.screens.route

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.ui.components.navigation.RoutesNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.extension.Container
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.SemiBlack
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RoutesNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun RouteDetailsScreen(
    navigator: DestinationsNavigator,
    route: Route
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = "Route Details",
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues = paddingValues)
                ) {
                    WebViewContent(route = route)
                    DetailsSectionContainer(route)
                }
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContent(route: Route) {
    val shape1 = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Card(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1
            )
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(shape1),
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        ),
        shape = shape1
    ) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = true
                clipToOutline = true
                webViewClient = WebViewClient()

                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setBackgroundColor(Color.Transparent.toArgb())
            }
        }, update = {
            it.loadUrl(route.routeWebUrl)
        })
    }

    val shape2 = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 12.dp,
        bottomEnd = 12.dp
    )

    Card(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.medium1)
            .fillMaxWidth()
            .clip(shape2),
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        ),
        shape = shape2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1,
                        bottom = MaterialTheme.spacing.medium1
                    )
                    .size(18.dp),
                tint = Dark
            )
            Text(
                text = "Use two fingers to move around the map.",
                color = Dark,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.small1,
                        end = MaterialTheme.spacing.medium1,
                        top = MaterialTheme.spacing.medium1,
                        bottom = MaterialTheme.spacing.medium1
                    )
            )
        }
    }
}

@Composable
private fun DetailsSectionContainer(route: Route) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium1)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = SemiBlack)
                .padding(MaterialTheme.spacing.medium1)
        ) {
            Text(
                text = "${route.routeNo} # ${route.routeName}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight(600)
            )
        }

        DetailsSectionContent(
            icon = R.drawable.ic_clock,
            iconDescription = "Clock",
            title = "Start Time (To DSC):",
            description = route.startTime,
            divider = ","
        )

        HorizontalDivider(
            color = Gray,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
        )

        DetailsSectionContent(
            icon = R.drawable.ic_clock,
            iconDescription = "Clock",
            title = "Departure Time (From DSC):",
            description = route.departureTime,
            divider = ","
        )

        HorizontalDivider(
            color = Gray,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
        )

        DetailsSectionContent(
            icon = R.drawable.ic_routing,
            iconDescription = "Route",
            title = "Route Details:",
            description = route.routeDetails,
            divider = "<>"
        )
    }
}

@Composable
private fun DetailsSectionContent(
    @DrawableRes icon: Int,
    iconDescription: String,
    title: String,
    description: String,
    divider: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1
            )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = iconDescription,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Dark, fontSize = 14.sp)) {
                val parts = description.split(divider)
                parts.forEachIndexed { index, part ->
                    append("\u2022 ${part.trim()}")
                    if (index != parts.size - 1) {
                        append("\n")
                    }
                }
            }
        },
        fontSize = 15.sp,
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.small1,
                bottom = MaterialTheme.spacing.medium1
            )
    )
}