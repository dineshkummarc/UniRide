package com.drdisagree.uniride.ui.screens.student.more.driver_list

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.DriverReviews
import com.drdisagree.uniride.data.models.Review
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.screens.destinations.DriverReviewsScreenDestination
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun DriverListScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.drivers_title),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                DriverListContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun DriverListContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues,
    driverListViewModel: DriverListViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        var showLoadingDialog by rememberSaveable { mutableStateOf(false) }
        val state by driverListViewModel.state.collectAsState(initial = Resource.Unspecified())

        when (state) {
            is Resource.Loading -> {
                showLoadingDialog = true
            }

            is Resource.Success -> {
                showLoadingDialog = false
                val driverReviews =
                    (state as Resource.Success<List<DriverReviews>>).data ?: emptyList()

                if (driverReviews.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = paddingValues)
                    ) {
                        itemsIndexed(
                            items = driverReviews,
                            key = { _, driverReviews -> driverReviews.id }
                        ) { index, driverReview ->
                            DriverListItem(
                                navigator = navigator,
                                index = index,
                                driverReviews = driverReview
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
                            text = stringResource(R.string.no_drivers_found),
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
private fun DriverListItem(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    index: Int,
    driverReviews: DriverReviews
) {
    val context = LocalContext.current
    val placeholder by remember { mutableIntStateOf(R.drawable.img_profile_pic_default) }
    val imageUrl by remember { mutableStateOf(driverReviews.about.profileImage) }

    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUrl + "_low")
        .diskCacheKey(imageUrl + "_low")
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .crossfade(250)
        .size(256)
        .build()

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                navigator.navigate(
                    DriverReviewsScreenDestination(
                        driverReviews = driverReviews
                    )
                )
            }
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
                .padding(MaterialTheme.spacing.medium1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Gray)
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = imageRequest,
                    placeholder = painterResource(id = R.drawable.img_loading),
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(100)),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = driverReviews.about.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (driverReviews.about.contactPhone.isNullOrEmpty()) {
                        if (driverReviews.about.contactEmail.isNullOrEmpty()) {
                            stringResource(R.string.contact_details_not_available)
                        } else {
                            driverReviews.about.contactEmail
                        }
                    } else {
                        driverReviews.about.contactPhone
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(stringResource(R.string.rating_colon))
                        }
                        append(getAverageRating(driverReviews.reviews))
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun getAverageRating(reviews: List<Review>): String {
    if (reviews.isEmpty()) {
        return "N/A"
    }

    val totalRating = reviews.sumOf { it.rating }
    val averageRating = totalRating.toDouble() / reviews.size
    val formattedRating = String.format("%.2f", averageRating)

    return when {
        formattedRating.endsWith(".00") -> "${averageRating.toInt()}/5"
        formattedRating.endsWith("0") -> formattedRating.dropLast(1) + "/5"
        else -> "$formattedRating/5"
    }.replace(",", "")
}