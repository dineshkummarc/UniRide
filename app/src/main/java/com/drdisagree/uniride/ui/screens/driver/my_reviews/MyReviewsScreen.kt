package com.drdisagree.uniride.ui.screens.driver.my_reviews

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Review
import com.drdisagree.uniride.ui.components.navigation.MainScreenGraph
import com.drdisagree.uniride.ui.components.transitions.SlideInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StarRatingBar
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.TimeUtils.millisToTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MainScreenGraph
@Destination(style = SlideInOutTransition::class)
@Composable
fun MyReviewsScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.reviews_about_me),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                MyReviewsScreenContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun MyReviewsScreenContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues,
    myReviewsViewModel: MyReviewsViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        var showLoadingDialog by rememberSaveable { mutableStateOf(false) }
        val state by myReviewsViewModel.state.collectAsState(initial = Resource.Unspecified())

        when (state) {
            is Resource.Loading -> {
                showLoadingDialog = true
            }

            is Resource.Success -> {
                showLoadingDialog = false
                val reviews = (state as Resource.Success<List<Review>>).data ?: emptyList()

                if (reviews.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = paddingValues)
                    ) {
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
                            text = "No reviews found about you!",
                        )
                    }
                }
            }

            is Resource.Error -> {
                showLoadingDialog = false

                Toast.makeText(
                    context,
                    (state as Resource.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Unit
            }
        }

        if (showLoadingDialog) {
            CircularProgressIndicator()
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

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (index != 0) {
            HorizontalDivider(
                color = LightGray,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
            )
        }

        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium3,
                    vertical = MaterialTheme.spacing.medium1
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.align(Alignment.Top)
            ) {
                Text(
                    text = "#$reviewIndex",
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                StarRatingBar(
                    rating = review.rating,
                    starSize = 8f,
                    starSpacing = 0.1f,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.small2)
                )
                Text(
                    text = review.message,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Submitted on: ")
                        }
                        append(review.timeStamp.millisToTime("dd/MM/yyyy - ${if (is24HourFormat) "HH:mm" else "hh:mm a"}"))
                    },
                    color = Dark,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}