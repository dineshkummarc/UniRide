package com.drdisagree.uniride.ui.screens.student.more.driver_list.driver_reviews

import android.text.format.DateFormat
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.models.DriverReviews
import com.drdisagree.uniride.data.models.Review
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StarRatingBar
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.components.views.shimmerBackground
import com.drdisagree.uniride.ui.screens.student.home.buslocation.ReviewSubmissionViewModel
import com.drdisagree.uniride.ui.screens.student.more.driver_list.getAverageRating
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.TimeUtils.millisToTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.UUID

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun DriverReviewsScreen(
    navigator: DestinationsNavigator,
    driverReviews: DriverReviews
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = driverReviews.about.name,
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                DriverReviewsContent(
                    paddingValues = paddingValues,
                    driver = driverReviews.about,
                    reviews = driverReviews.reviews
                )
            }
        )
    }
}

@Composable
private fun DriverReviewsContent(
    paddingValues: PaddingValues,
    driver: Driver,
    reviews: List<Review>,
    reviewSubmissionViewModel: ReviewSubmissionViewModel = hiltViewModel(),
) {
    val summaryState by reviewSubmissionViewModel.summary.collectAsState()
    val avgRating by remember { mutableStateOf(getAverageRating(reviews)) }

    LaunchedEffect(driver.id) {
        reviewSubmissionViewModel.fetchSummary(driver.id)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (reviews.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
            ) {
                item {
                    ReviewListItem(
                        index = -1,
                        review = Review(
                            uuid = UUID.randomUUID().toString(),
                            submittedBy = Student(),
                            message = when (summaryState) {
                                is Resource.Loading -> stringResource(R.string.loading)
                                is Resource.Success -> (summaryState as Resource.Success<String>).data
                                is Resource.Error -> (summaryState as Resource.Error).message
                                else -> stringResource(R.string.no_reviews_yet)
                            } ?: stringResource(R.string.loading),
                            rating = if (avgRating == "N/A") 0 else avgRating.split("/")[0].toInt(),
                            timeStamp = 0
                        ),
                        reviewIndex = 0
                    )
                }

                itemsIndexed(
                    items = reviews,
                    key = { _, review -> review.uuid }
                ) { index, review ->
                    ReviewListItem(
                        index = index,
                        review = review,
                        reviewIndex = reviews.size - index
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_reviews_found_about_this_driver),
                )
            }
        }
    }
}

@Composable
private fun ReviewListItem(
    modifier: Modifier = Modifier,
    index: Int,
    review: Review,
    reviewIndex: Int
) {
    val context = LocalContext.current
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val loadingReview = review.message == stringResource(R.string.loading)
    val showShimmer by rememberUpdatedState(reviewIndex == 0 && loadingReview)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (index != -1) {
            HorizontalDivider(
                color = LightGray,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
            )
        }

        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .animateContentSize()
                .then(
                    if (index == -1) {
                        Modifier
                            .padding(
                                horizontal = MaterialTheme.spacing.medium1,
                                vertical = MaterialTheme.spacing.small2
                            )
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0x1A4597E4),
                                        Color(0x1AC7667C)
                                    )
                                ),
                                shape = RoundedCornerShape(MaterialTheme.spacing.small3)
                            )
                            .padding(MaterialTheme.spacing.small2)
                    } else {
                        Modifier.padding(
                            horizontal = MaterialTheme.spacing.medium3,
                            vertical = MaterialTheme.spacing.medium1
                        )
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(40.dp)
            ) {
                Text(
                    text = if (reviewIndex != 0) "#$reviewIndex" else "AI",
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = if (reviewIndex != 0) {
                        Modifier
                    } else {
                        Modifier
                            .padding(start = 3.dp)
                            .drawBehind {
                            val strokeWidthPx = 2.dp.toPx()
                            val verticalOffset = size.height - 3.sp.toPx()
                            val lineExtension = 2.sp.toPx()

                            drawLine(
                                color = Black,
                                strokeWidth = strokeWidthPx,
                                start = Offset(-lineExtension, verticalOffset),
                                end = Offset(size.width + lineExtension, verticalOffset),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                if (index != 0 || review.rating != 0) {
                    StarRatingBar(
                        rating = review.rating,
                        starSize = 8f,
                        starSpacing = 0.1f,
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.small2)
                    )
                }
                if (showShimmer) {
                    ShimmerPlaceholderReview(
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = review.message,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (reviewIndex != 0) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(stringResource(R.string.submitted_on_colon))
                            }
                            append(review.timeStamp.millisToTime("dd/MM/yyyy - ${if (is24HourFormat) "HH:mm" else "hh:mm a"}"))
                        },
                        color = Dark,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.img_gemini),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 4.dp)
                                .size(20.dp),
                        )
                        Text(
                            text = stringResource(id = R.string.summarized_by_gemini),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxWidth(1f),
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF4597E4), Color(0xFFC7667C))
                                ),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerPlaceholderReview(modifier: Modifier = Modifier) {
    Column(modifier = modifier.alpha(0.8f)) {
        Text(
            text = "",
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .shimmerBackground(cornerRadius = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "",
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .shimmerBackground(cornerRadius = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "",
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .shimmerBackground(cornerRadius = 4.dp)
        )
    }
}