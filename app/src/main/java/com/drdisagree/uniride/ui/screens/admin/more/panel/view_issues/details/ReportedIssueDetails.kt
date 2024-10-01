package com.drdisagree.uniride.ui.screens.admin.more.panel.view_issues.details

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Issue
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.ButtonPrimary
import com.drdisagree.uniride.ui.components.views.ButtonSecondary
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.LoadingDialog
import com.drdisagree.uniride.ui.components.views.StyledAlertDialog
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.screens.destinations.ReportedIssuesDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.ColorUtils.getIssuePillColors
import com.drdisagree.uniride.utils.TimeUtils.millisToTime
import com.drdisagree.uniride.utils.viewmodels.AccountStatusViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun ReportedIssueDetails(
    navigator: DestinationsNavigator,
    issue: Issue
) {
    var openDialog by remember { mutableStateOf(false) }

    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(R.string.reported_issue),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = "Delete",
                                tint = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    },
                    endIconClick = {
                        openDialog = true
                    }
                )
            },
            content = { paddingValues ->
                ReportedIssueDetailsContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    issue = issue,
                    openDialog = openDialog,
                    onCloseDialog = {
                        openDialog = false
                    }
                )
            }
        )
    }
}

@Composable
private fun ReportedIssueDetailsContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    issue: Issue,
    openDialog: Boolean,
    onCloseDialog: () -> Unit,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel(),
    reportedIssueDetailsViewModel: ReportedIssueDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()

    val (_, pillTextColor) = remember(issue.resolved) {
        getIssuePillColors(issue.resolved)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.medium1)
            ) {
                Text(
                    text = "Issue ID:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                BasicText(
                    text = issue.uuid,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Dark,
                        fontSize = 15.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                clipboardManager.setText(AnnotatedString(issue.uuid))

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
                    text = "Issue Type:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = issue.type,
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = "Submitted On:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = issue.timeStamp.millisToTime("dd/MM/yyyy - ${if (is24HourFormat) "HH:mm" else "hh:mm a"}"),
                    color = Dark,
                    fontSize = 15.sp
                )
                Text(
                    text = "Status:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = if (issue.resolved) "Resolved" else "Unresolved",
                    color = pillTextColor,
                    fontSize = 15.sp
                )
                Text(
                    text = "Description:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                Text(
                    text = issue.description,
                    color = Dark,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Justify
                )
                Text(
                    text = "Contact Info:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium1)
                )
                BasicText(
                    text = issue.contactInfo,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Dark,
                        fontSize = 15.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                clipboardManager.setText(AnnotatedString(issue.contactInfo))

                                Toast.makeText(
                                    context,
                                    "Copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                )

                if (!issue.resolved) {
                    ButtonPrimary(
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.large2)
                            .fillMaxWidth(),
                        text = "Mark as Resolved",
                        onClick = {
                            reportedIssueDetailsViewModel.editIssue(
                                issue.copy(
                                    resolved = true
                                )
                            )
                        }
                    )
                } else {
                    ButtonSecondary(
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.large2)
                            .fillMaxWidth(),
                        text = "Mark as Unresolved",
                        onClick = {
                            reportedIssueDetailsViewModel.editIssue(
                                issue.copy(
                                    resolved = false
                                )
                            )
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
                                    "Issue status updated",
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

                LaunchedEffect(Unit) {
                    reportedIssueDetailsViewModel.deleteState.collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                showLoadingDialog = true
                            }

                            is Resource.Success -> {
                                showLoadingDialog = false

                                Toast.makeText(
                                    context,
                                    result.data,
                                    Toast.LENGTH_SHORT
                                ).show()

                                navigator.popBackStack(
                                    route = ReportedIssuesDestination,
                                    inclusive = false
                                )
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

                if (openDialog) {
                    StyledAlertDialog(
                        title = "Are you sure?",
                        message = "This action cannot be undone. Delete this issue?",
                        confirmButtonText = "Delete",
                        dismissButtonText = "Cancel",
                        onConfirmButtonClick = {
                            onCloseDialog()
                            reportedIssueDetailsViewModel.deleteIssue(issue.uuid)
                        },
                        onDismissButtonClick = {
                            onCloseDialog()
                        },
                        onDismissRequest = {
                            onCloseDialog()
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
                    text = "You are not an admin"
                )
            }
        }
    }
}