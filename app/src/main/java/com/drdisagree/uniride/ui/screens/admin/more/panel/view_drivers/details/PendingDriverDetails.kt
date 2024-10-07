package com.drdisagree.uniride.ui.screens.admin.more.panel.view_drivers.details

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.enums.AccountStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ButtonSecondary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.ColorUtils.getDriverPillColors
import com.drdisagree.uniride.utils.TimeUtils.millisToTime
import com.drdisagree.uniride.utils.openUrl
import com.drdisagree.uniride.viewmodels.AccountStatusViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers


@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun PendingDriverDetails(
    navigator: DestinationsNavigator,
    driver: Driver
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(R.string.pending_driver),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                PendingDriverDetailsContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    driver = driver
                )
            }
        )
    }
}

@Composable
private fun PendingDriverDetailsContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    driver: Driver,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel(),
    reportedIssueDetailsViewModel: PendingDriverDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()

    val (_, pillTextColor) = remember(driver.accountStatus) {
        getDriverPillColors(driver.accountStatus)
    }

    when (isAdminState) {
        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(Color.White)
                        .wrapContentSize()
                )
            }
        }

        true -> {
            val placeholder by remember { mutableIntStateOf(R.drawable.img_profile_pic_default) }
            val profileImageUrl by remember { mutableStateOf(driver.profileImage) }

            val nidFrontUrl by remember { mutableStateOf(driver.documents[0]) }
            val nidBackUrl by remember { mutableStateOf(driver.documents[1]) }
            val drivingLicenseFrontUrl by remember { mutableStateOf(driver.documents[2]) }
            val drivingLicenseBackUrl by remember { mutableStateOf(driver.documents[3]) }

            var isNidFrontImageLoaded by remember { mutableStateOf(false) }
            var isNidBackImageLoaded by remember { mutableStateOf(false) }
            var isDrivingLicenseFrontImageLoaded by remember { mutableStateOf(false) }
            var isDrivingLicenseBackImageLoaded by remember { mutableStateOf(false) }

            val nidFrontImageRequest = ImageRequest.Builder(context)
                .data(nidFrontUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(nidFrontUrl + "_low")
                .diskCacheKey(nidFrontUrl + "_low")
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .crossfade(250)
                .size(128)
                .listener(
                    onStart = {
                        isNidFrontImageLoaded = false
                    },
                    onSuccess = { _, _ ->
                        isNidFrontImageLoaded = true
                    },
                    onError = { _, _ ->
                        isNidFrontImageLoaded = false
                    }
                )
                .build()

            val nidBackImageRequest = ImageRequest.Builder(context)
                .data(nidBackUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(nidBackUrl + "_low")
                .diskCacheKey(nidBackUrl + "_low")
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .crossfade(250)
                .size(128)
                .listener(
                    onStart = {
                        isNidBackImageLoaded = false
                    },
                    onSuccess = { _, _ ->
                        isNidBackImageLoaded = true
                    },
                    onError = { _, _ ->
                        isNidBackImageLoaded = false
                    }
                )
                .build()

            val drivingLicenseFrontImageRequest = ImageRequest.Builder(context)
                .data(drivingLicenseFrontUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(drivingLicenseFrontUrl + "_low")
                .diskCacheKey(drivingLicenseFrontUrl + "_low")
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .crossfade(250)
                .size(128)
                .listener(
                    onStart = {
                        isDrivingLicenseFrontImageLoaded = false
                    },
                    onSuccess = { _, _ ->
                        isDrivingLicenseFrontImageLoaded = true
                    },
                    onError = { _, _ ->
                        isDrivingLicenseFrontImageLoaded = false
                    }
                )
                .build()

            val drivingLicenseBackImageRequest = ImageRequest.Builder(context)
                .data(drivingLicenseBackUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(drivingLicenseBackUrl + "_low")
                .diskCacheKey(drivingLicenseBackUrl + "_low")
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .crossfade(250)
                .size(128)
                .listener(
                    onStart = {
                        isDrivingLicenseBackImageLoaded = false
                    },
                    onSuccess = { _, _ ->
                        isDrivingLicenseBackImageLoaded = true
                    },
                    onError = { _, _ ->
                        isDrivingLicenseBackImageLoaded = false
                    }
                )
                .build()

            val profileImageRequest = ImageRequest.Builder(context)
                .data(profileImageUrl)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(profileImageUrl + "_low")
                .diskCacheKey(profileImageUrl + "_low")
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.medium1)
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.medium3)
                        .size(160.dp)
                        .clip(RoundedCornerShape(100))
                        .background(Gray)
                        .padding(MaterialTheme.spacing.small1)
                        .align(Alignment.CenterHorizontally)
                ) {
                    AsyncImage(
                        model = profileImageRequest,
                        placeholder = painterResource(id = R.drawable.img_loading),
                        contentDescription = stringResource(R.string.profile_picture),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(100)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Text(
                    text = stringResource(R.string.account_id_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                BasicText(
                    text = driver.id,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Dark,
                        fontSize = 15.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                clipboardManager.setText(AnnotatedString(driver.id))

                                Toast.makeText(
                                    context,
                                    "Copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                )
                Text(
                    text = stringResource(R.string.account_status_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = when (driver.accountStatus) {
                        AccountStatus.APPROVED -> stringResource(R.string.approved)
                        AccountStatus.PENDING -> stringResource(R.string.pending)
                        AccountStatus.REJECTED -> stringResource(R.string.rejected)
                    },
                    color = pillTextColor,
                    fontSize = 15.sp
                )
                Text(
                    text = stringResource(R.string.created_on_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = driver.timeStamp.millisToTime("dd/MM/yyyy - ${if (is24HourFormat) "HH:mm" else "hh:mm a"}"),
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = stringResource(R.string.name_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = driver.name,
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = if (driver.email == null) {
                        stringResource(R.string.phone_colon)
                    } else {
                        stringResource(R.string.email_colon)
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = driver.email ?: driver.phone ?: stringResource(R.string.unknown),
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = stringResource(R.string.contact_information_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        ) {
                            append(stringResource(R.string.phone_colon))
                        }
                        append(if (driver.contactPhone.isNullOrEmpty()) stringResource(R.string.not_provided) else driver.contactPhone)
                        append("\n")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        ) {
                            append(stringResource(R.string.email_colon))
                        }
                        append(if (driver.contactEmail.isNullOrEmpty()) stringResource(R.string.not_provided) else driver.contactEmail)
                    },
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = stringResource(R.string.nid_card_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.small1),
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f, true)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                openUrl(context, nidFrontUrl)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = LightGray
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            AsyncImage(
                                model = nidFrontImageRequest,
                                placeholder = painterResource(id = R.drawable.img_loading),
                                contentDescription = stringResource(R.string.nid_card_front),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            if (isNidFrontImageLoaded) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(MaterialTheme.spacing.small1)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .size(36.dp),
                                    onClick = {
                                        openUrl(context, nidFrontUrl)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Download,
                                        contentDescription = stringResource(R.string.download_nid_card_front),
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f, true)
                            .padding(start = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                openUrl(context, nidBackUrl)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = LightGray
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            AsyncImage(
                                model = nidBackImageRequest,
                                placeholder = painterResource(id = R.drawable.img_loading),
                                contentDescription = stringResource(R.string.nid_card_back),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            if (isNidBackImageLoaded) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(MaterialTheme.spacing.small1)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .size(36.dp),
                                    onClick = {
                                        openUrl(context, nidBackUrl)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Download,
                                        contentDescription = stringResource(R.string.download_nid_card_back),
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.driving_license_colon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.small1),
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.9f, true)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                openUrl(context, drivingLicenseFrontUrl)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = LightGray
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            AsyncImage(
                                model = drivingLicenseFrontImageRequest,
                                placeholder = painterResource(id = R.drawable.img_loading),
                                contentDescription = stringResource(R.string.driving_license_front),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            if (isDrivingLicenseFrontImageLoaded) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(MaterialTheme.spacing.small1)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .size(36.dp),
                                    onClick = {
                                        openUrl(context, drivingLicenseFrontUrl)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Download,
                                        contentDescription = stringResource(R.string.download_driving_license_front),
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.9f, true)
                            .padding(start = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                openUrl(context, drivingLicenseBackUrl)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = LightGray
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            AsyncImage(
                                model = drivingLicenseBackImageRequest,
                                placeholder = painterResource(id = R.drawable.img_loading),
                                contentDescription = stringResource(R.string.driving_license_back),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            if (isDrivingLicenseBackImageLoaded) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(MaterialTheme.spacing.small1)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .size(36.dp),
                                    onClick = {
                                        openUrl(context, drivingLicenseBackUrl)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Download,
                                        contentDescription = stringResource(R.string.download_driving_license_back),
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                var approveAccountDialog by remember { mutableStateOf(false) }
                var rejectAccountDialog by remember { mutableStateOf(false) }

                if (driver.accountStatus != AccountStatus.APPROVED) {
                    Row(
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.large2)
                            .fillMaxWidth()
                    ) {
                        if (driver.accountStatus != AccountStatus.REJECTED) {
                            ButtonSecondary(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                text = stringResource(R.string.reject),
                                onClick = {
                                    rejectAccountDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium1))
                        }
                        ButtonPrimary(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            text = stringResource(R.string.approve),
                            onClick = {
                                approveAccountDialog = true
                            }
                        )
                    }
                } else {
                    ButtonSecondary(
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.large2)
                            .fillMaxWidth(),
                        text = stringResource(R.string.reject),
                        onClick = {
                            rejectAccountDialog = true
                        }
                    )
                }

                var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    reportedIssueDetailsViewModel.editState.collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                showLoadingDialog = true
                            }

                            is Resource.Success -> {
                                showLoadingDialog = false

                                Toast.makeText(
                                    context,
                                    "Account status updated",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navigator.navigateUp()
                            }

                            is Resource.Error -> {
                                showLoadingDialog = false

                                Toast.makeText(
                                    context,
                                    result.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                showLoadingDialog = false
                            }
                        }
                    }
                }

                if (showLoadingDialog) {
                    LoadingDialog()
                }

                if (approveAccountDialog) {
                    StyledAlertDialog(
                        title = stringResource(R.string.approve_account),
                        message = stringResource(R.string.approve_account_confirmation),
                        confirmButtonText = stringResource(R.string.approve),
                        dismissButtonText = stringResource(R.string.cancel),
                        onConfirmButtonClick = {
                            approveAccountDialog = false

                            reportedIssueDetailsViewModel.updateStatus(
                                driver.copy(
                                    accountStatus = AccountStatus.APPROVED
                                )
                            )
                        },
                        onDismissButtonClick = {
                            approveAccountDialog = false
                        },
                        onDismissRequest = {
                            approveAccountDialog = false
                        }
                    )
                } else if (rejectAccountDialog) {
                    StyledAlertDialog(
                        title = stringResource(R.string.reject_account),
                        message = stringResource(R.string.reject_account_confirmation),
                        confirmButtonText = stringResource(R.string.reject),
                        dismissButtonText = stringResource(R.string.cancel),
                        onConfirmButtonClick = {
                            rejectAccountDialog = false

                            reportedIssueDetailsViewModel.updateStatus(
                                driver.copy(
                                    accountStatus = AccountStatus.REJECTED
                                )
                            )
                        },
                        onDismissButtonClick = {
                            rejectAccountDialog = false
                        },
                        onDismissRequest = {
                            rejectAccountDialog = false
                        }
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.you_are_not_an_admin)
                )
            }
        }
    }
}