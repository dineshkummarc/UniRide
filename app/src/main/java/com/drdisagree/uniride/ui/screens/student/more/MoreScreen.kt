package com.drdisagree.uniride.ui.screens.student.more

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.extension.Container
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.screens.destinations.OnBoardingScreenDestination
import com.drdisagree.uniride.ui.screens.student.account.GoogleAuthUiClient
import com.drdisagree.uniride.ui.screens.student.main.getRootNavigator
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@MoreNavGraph(start = true)
@Destination(style = FadeInOutTransition::class)
@Composable
fun MoreScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthUiClient by remember(context) {
        lazy {
            GoogleAuthUiClient(
                context = context.applicationContext,
                oneTapClient = Identity.getSignInClient(context.applicationContext)
            )
        }
    }

    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(id = R.string.nav_more),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = "Logout",
                                tint = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    },
                    endIconClick = {
                        scope.launch {
                            googleAuthUiClient.signOut()

                            navigator.popBackStack()
                            getRootNavigator().popBackStack()
                            getRootNavigator().navigate(
                                OnBoardingScreenDestination()
                            ) {
                                popUpTo(NavGraphs.root.startRoute)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            },
            content = { paddingValues ->
                MoreContent(
                    paddingValues = paddingValues,
                    student = googleAuthUiClient.getSignedInUser()
                )
            }
        )
    }
}

@Composable
private fun MoreContent(
    paddingValues: PaddingValues,
    student: Student?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileSection(student = student)

        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.medium1,
                top = MaterialTheme.spacing.medium1
            )
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(MaterialTheme.spacing.medium1),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium1),
            verticalItemSpacing = MaterialTheme.spacing.medium1
        ) {
            items(
                count = 4,
                key = { it }
            ) {
                MoreListItem(
                    icon = R.drawable.ic_map,
                    title = R.string.my_location,
                    subtitle = R.string.find_my_location
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(
    student: Student?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.medium1),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholder = R.drawable.img_profile_pic_default
        val imageUrl = student?.profilePictureUrl

        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .dispatcher(Dispatchers.IO)
            .memoryCacheKey(imageUrl)
            .diskCacheKey(imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .fallback(placeholder)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(100))
                .background(Gray)
                .padding(MaterialTheme.spacing.small2)
        ) {
            AsyncImage(
                model = imageRequest,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.Crop,
            )
        }

        Text(
            text = student?.userName ?: "Anonymous",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small2)
        )
        Text(
            text = Firebase.auth.currentUser?.email ?: "unknown@diu.edu.bd",
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = Dark
        )
    }
}

@Composable
private fun MoreListItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .heightIn(min = 80.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(LightGray)
            .clickable {
                onClick?.invoke()
            }
            .padding(MaterialTheme.spacing.medium1)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.medium1)
                .size(26.dp),
            tint = Color.Black
        )
        Text(
            text = stringResource(id = title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = subtitle),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = Dark
        )
    }
}