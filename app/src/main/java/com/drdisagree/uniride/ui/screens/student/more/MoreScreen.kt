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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.drdisagree.uniride.ui.screens.admin.account.AccountStatusViewModel
import com.drdisagree.uniride.ui.screens.destinations.AdminPanelDestination
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
                    navigator = navigator,
                    student = googleAuthUiClient.getSignedInUser()
                )
            }
        )
    }
}

@Composable
private fun MoreContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    student: Student?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileSection(student = student)
        QuickActionsSection(navigator = navigator)
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
private fun QuickActionsSection(
    navigator: DestinationsNavigator,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel()
) {
    Text(
        text = "Quick Actions",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1,
            bottom = MaterialTheme.spacing.medium1
        )
    )

    Row(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium1)
    ) {
        QuickActionsItem(
            icon = R.drawable.ic_map,
            title = R.string.my_location_title,
            subtitle = R.string.my_location_summary,
            modifier = Modifier
                .padding(start = MaterialTheme.spacing.medium1)
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium1))

        QuickActionsItem(
            icon = R.drawable.ic_warning,
            title = R.string.emergency_title,
            subtitle = R.string.emergency_summary,
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.medium1)
                .weight(1f)
        )
    }

    Row(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium1)
    ) {
        QuickActionsItem(
            icon = R.drawable.ic_message,
            title = R.string.chat_box_title,
            subtitle = R.string.chat_box_summary,
            modifier = Modifier
                .padding(start = MaterialTheme.spacing.medium1)
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium1))

        QuickActionsItem(
            icon = R.drawable.ic_sms_edit,
            title = R.string.report_title,
            subtitle = R.string.report_summary,
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.medium1)
                .weight(1f)
        )
    }

    val isAdministrator by rememberSaveable {
        mutableStateOf(accountStatusViewModel.isUserAdmin())
    }

    if (isAdministrator == true || isAdministrator == null) { // TODO: Show only if admin
        Row(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium1)
        ) {
            QuickActionsItem(
                icon = R.drawable.ic_category,
                title = R.string.admin_panel_title,
                subtitle = R.string.admin_panel_summary,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.medium1)
                    .weight(1f),
                onClick = {
                    navigator.navigate(AdminPanelDestination)
                }
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium1))

            QuickActionsItem(
                icon = R.drawable.ic_sms_edit,
                title = R.string.report_title,
                subtitle = R.string.report_summary,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.medium1)
                    .weight(1f)
                    .alpha(0f) // Hide the item, reveal in future when new item needed
            )
        }
    }
}

@Composable
private fun QuickActionsItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
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