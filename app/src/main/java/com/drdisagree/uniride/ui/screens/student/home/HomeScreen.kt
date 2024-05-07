package com.drdisagree.uniride.ui.screens.student.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Notice
import com.drdisagree.uniride.data.utils.Constant.STUDENT_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.navigation.HomeNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.RequestGpsEnable
import com.drdisagree.uniride.ui.components.views.RequestLocationPermission
import com.drdisagree.uniride.ui.components.views.TopAppBarNoButton
import com.drdisagree.uniride.ui.components.views.areLocationPermissionsGranted
import com.drdisagree.uniride.ui.screens.destinations.CurrentLocationScreenDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.viewmodels.GpsStateManager
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import eu.wewox.textflow.TextFlow
import kotlin.random.Random

@HomeNavGraph(start = true)
@Destination(style = FadeInOutTransition::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarNoButton(
                    title = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.Cursive
                )
            },
            content = { paddingValues ->
                HomeContent(
                    navigator = navigator,
                    paddingValues = paddingValues
                )
            }
        )
    }
}

@Composable
private fun HomeContent(
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues
) {
    HandlePermissions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        NoticeBoard()
        NearbyBuses(navigator = navigator)
    }
}

@Composable
private fun HandlePermissions(
    gpsStateManager: GpsStateManager = hiltViewModel()
) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(false)
    }
    val gpsRequested by remember {
        mutableStateOf(gpsStateManager.gpsRequested.value)
    }

    LaunchedEffect(Unit) {
        Prefs.putString(WHICH_USER_COLLECTION, STUDENT_COLLECTION)
        permissionGranted = areLocationPermissionsGranted(context)
    }

    RequestLocationPermission(
        onPermissionGranted = { permissionGranted = true },
        onPermissionDenied = { permissionGranted = false }) {
        Toast.makeText(
            context,
            "Please grant location permission",
            Toast.LENGTH_SHORT
        ).show()
    }

    if (permissionGranted && !gpsRequested) {
        RequestGpsEnable(
            context = context,
            onGpsEnabled = { },
            onGpsDisabled = {
                Toast.makeText(
                    context,
                    "Please enable GPS",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        gpsStateManager.setGpsRequested(true)
    }
}

@Composable
private fun NoticeBoard(
    modifier: Modifier = Modifier,
    noticeBoardViewModel: NoticeBoardViewModel = hiltViewModel()
) {
    val notices by noticeBoardViewModel.noticeBoard.collectAsState(initial = Resource.Unspecified())

    LaunchedEffect(Unit) {
        noticeBoardViewModel.getLastAnnouncement()
    }

    Text(
        text = "Notice Board",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.medium1,
            top = MaterialTheme.spacing.medium1
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium1)
            .clip(MaterialTheme.shapes.medium)
            .background(LightGray)
            .padding(MaterialTheme.spacing.medium1)
    ) {
        when (notices) {
            is Resource.Loading -> {
                Text(
                    text = "Loading...",
                    color = Dark,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )
            }

            is Resource.Success -> {
                (notices as Resource.Success<Notice>).data?.let {
                    TextFlow(
                        text = it.announcement,
                        color = Dark,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_announcement),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = MaterialTheme.spacing.small3)
                                .width(80.dp)
                        )
                    }
                }
            }

            is Resource.Error -> {
                (notices as Resource.Error<Notice>).message?.let {
                    Text(
                        text = it,
                        color = Dark,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun NearbyBuses(
    navigator: DestinationsNavigator
) {
    Text(
        text = "Nearby Buses",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            top = MaterialTheme.spacing.medium1,
            start = MaterialTheme.spacing.medium1,
            bottom = MaterialTheme.spacing.medium1
        )
    )
    repeat(10) { index ->
        val routeNo = remember {
            "${listOf("Surjomukhi", "Dolpin", "Rojonigondha").random()}-${Random.nextInt(1, 10)}"
        }
        NearbyBusListItem(
            index = index,
            routeNo = routeNo,
            routeName = "DSC to Dhanmondi",
            onClick = {
                navigator.navigate(
                    CurrentLocationScreenDestination()
                )
            }
        )
    }
}

@Composable
private fun NearbyBusListItem(
    modifier: Modifier = Modifier,
    index: Int,
    routeNo: String,
    routeName: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick?.invoke()
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
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium3,
                    vertical = MaterialTheme.spacing.medium1
                ),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = "Map with marker image",
                modifier = Modifier
                    .padding(end = 16.dp, top = 2.dp)
                    .size(28.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .padding(end = MaterialTheme.spacing.medium1),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = routeNo,
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Dhanmondi <> DSC",
                    color = Dark,
                    fontSize = 14.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                            append("Currently at: ")
                        }
                        append(listOf("Dhanmondi", "Mirpur", "Birulia").random())
                    },
                    color = Dark,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Departed at",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = listOf("07:05 AM", "07:20 AM", "07:35 AM").random(),
                    color = Dark,
                    fontSize = 14.sp
                )
            }
        }
    }
}