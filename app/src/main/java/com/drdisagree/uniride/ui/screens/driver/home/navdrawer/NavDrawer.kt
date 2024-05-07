package com.drdisagree.uniride.ui.screens.driver.home.navdrawer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.screens.destinations.EditProfileScreenDestination
import com.drdisagree.uniride.ui.screens.destinations.OnBoardingScreenDestination
import com.drdisagree.uniride.ui.screens.driver.home.DriverHomeViewModel
import com.drdisagree.uniride.ui.screens.driver.login.DriverLoginViewModel
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.Gray15
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    navigator: DestinationsNavigator,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    driverHomeViewModel: DriverHomeViewModel,
    driverLoginViewModel: DriverLoginViewModel = hiltViewModel()
) {
    DrawerHeader(
        navigator = navigator,
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        driverHomeViewModel = driverHomeViewModel
    )
    Spacer(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1,
                bottom = MaterialTheme.spacing.medium1
            )
            .height(1.dp)
            .fillMaxWidth()
            .background(Gray15)
    )
    DrawerBody(
        items = listOf(
            MenuItemModel(
                id = "home",
                title = "Home",
                contentDescription = "Go to home screen",
                icon = Icons.Default.Home,
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            ),
            MenuItemModel(
                id = "settings",
                title = "Settings",
                contentDescription = "Go to settings screen",
                icon = Icons.Default.Settings,
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            ),
            MenuItemModel(
                id = "help",
                title = "Help",
                contentDescription = "Get help",
                icon = Icons.Default.Info,
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            ),
            MenuItemModel(
                id = "sign_out",
                title = "Sign out",
                contentDescription = "Sign out",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()

                        driverLoginViewModel.signOut()

                        navigator.navigate(
                            OnBoardingScreenDestination()
                        ) {
                            popUpTo(NavGraphs.root.startRoute)
                            launchSingleTop = true
                        }
                        Prefs.clearPref(Constant.WHICH_USER_COLLECTION)
                    }
                }
            )
        )
    )
}

@Composable
private fun DrawerHeader(
    navigator: DestinationsNavigator,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    driverHomeViewModel: DriverHomeViewModel
) {
    val context = LocalContext.current
    var driver: Driver? by remember { mutableStateOf(null) }
    driverHomeViewModel.getSignedInDriver()

    LaunchedEffect(driverHomeViewModel.getDriver) {
        driverHomeViewModel.getDriver.collect { result ->
            when (result) {
                is Resource.Success -> {
                    driver = result.data
                }

                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Unit
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = MaterialTheme.spacing.extraLarge1,
                bottom = MaterialTheme.spacing.medium3,
                start = MaterialTheme.spacing.medium1,
                end = MaterialTheme.spacing.medium1
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholder = R.drawable.img_profile_pic_default
        val imageUrl = driver?.profileImage

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
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
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
            IconButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Blue)
                    .size(30.dp),
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navigator.navigate(EditProfileScreenDestination)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            MaterialTheme.spacing.small1
                        ),
                    tint = Color.White
                )
            }
        }

        Text(
            text = driver?.name ?: "Unknown",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small3)
        )
        Text(
            text = driver?.email ?: "unknown@diu.edu.bd",
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = Dark
        )
    }
}

@Composable
private fun DrawerBody(
    items: List<MenuItemModel>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 16.sp)
) {
    LazyColumn(modifier) {
        items(
            count = items.size,
            key = {
                items[it].id
            }
        ) { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = items[item].title,
                        style = itemTextStyle,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                selected = false,
                onClick = {
                    items[item].onClick?.invoke()
                },
                icon = {
                    Icon(
                        imageVector = items[item].icon,
                        contentDescription = items[item].contentDescription
                    )
                },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small2)
            )
        }
    }
}