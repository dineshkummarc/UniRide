package com.drdisagree.uniride.ui.screens.admin.more.panel.view_issues

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Issue
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.StyledTextField
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButtonAndEndIcon
import com.drdisagree.uniride.ui.screens.destinations.ReportedIssueDetailsDestination
import com.drdisagree.uniride.ui.theme.Dark
import com.drdisagree.uniride.ui.theme.spacing
import com.drdisagree.uniride.utils.ColorUtils.getIssuePillColors
import com.drdisagree.uniride.utils.TimeUtils.millisToTime
import com.drdisagree.uniride.viewmodels.AccountStatusViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun ReportedIssues(
    navigator: DestinationsNavigator
) {
    var openSearch by rememberSaveable { mutableStateOf(false) }

    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButtonAndEndIcon(
                    title = stringResource(R.string.reported_issues),
                    onBackClick = {
                        navigator.navigateUp()
                    },
                    endIcon = {
                        Row {
                            Icon(
                                imageVector = if (!openSearch) Icons.Rounded.Search else Icons.Rounded.SearchOff,
                                contentDescription = stringResource(R.string.search),
                                tint = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    },
                    endIconClick = { openSearch = !openSearch }
                )
            },
            content = { paddingValues ->
                ReportedIssuesContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    isShowingSearch = openSearch
                )
            }
        )
    }
}

@Composable
private fun ReportedIssuesContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    isShowingSearch: Boolean = false,
    accountStatusViewModel: AccountStatusViewModel = hiltViewModel(),
    reportedIssuesViewModel: ReportedIssuesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isAdminState by accountStatusViewModel.isAdmin.collectAsState()
    val issues by reportedIssuesViewModel.issues.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(isShowingSearch) {
        if (!isShowingSearch) {
            searchQuery = ""
        }
    }

    val filteredIssues = remember(issues, searchQuery) {
        when (issues) {
            is Resource.Success -> {
                val issueList = (issues as Resource.Success<List<Issue>>).data.orEmpty()
                val trimmedQuery = searchQuery.trim()

                if (isShowingSearch && trimmedQuery.isNotEmpty()) {
                    issueList.filter { issue ->
                        issue.type.contains(trimmedQuery, ignoreCase = true) ||
                                issue.uuid.contains(trimmedQuery, ignoreCase = true)
                    }
                } else {
                    issueList
                }
            }

            else -> emptyList()
        }
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
            ) {
                if (isShowingSearch) {
                    StyledTextField(
                        placeholder = stringResource(R.string.search_issue),
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.medium1),
                        onValueChange = {
                            searchQuery = it
                        },
                        inputText = searchQuery,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        leadingIcon = Icons.Rounded.Search,
                        trailingIcon = if (searchQuery.isNotEmpty()) Icons.Rounded.Clear else null,
                        trailingIconOnClick = {
                            searchQuery = ""
                        }
                    )
                }

                when (issues) {
                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
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

                    is Resource.Success -> {
                        if (filteredIssues.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    count = filteredIssues.size,
                                    key = { filteredIssues[it].uuid }
                                ) { index ->
                                    IssueListItem(
                                        index = index,
                                        navigator = navigator,
                                        issue = filteredIssues[index]
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = MaterialTheme.spacing.large1),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = stringResource(R.string.no_reported_issue_found)
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        (issues as Resource.Error<*>).message?.let {
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    else -> {}
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

@Composable
fun IssueListItem(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    index: Int,
    issue: Issue
) {
    val (pillBackgroundColor, pillTextColor) = remember(issue.resolved) {
        getIssuePillColors(issue.resolved)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                navigator.navigate(
                    ReportedIssueDetailsDestination(
                        issue = issue
                    )
                )
            }
    ) {
        if (index != 0) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium1)
            )
        }

        Column(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .background(pillBackgroundColor)
                .padding(
                    horizontal = MaterialTheme.spacing.medium3,
                    vertical = MaterialTheme.spacing.medium1
                ),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    ) {
                        append(stringResource(R.string.id_colon))
                    }
                    append(issue.uuid)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Dark
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    ) {
                        append(stringResource(R.string.issue_type_colon))
                    }
                    append(issue.type)
                },
                color = Dark,
                fontSize = 14.sp
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    ) {
                        append(stringResource(R.string.submitted_on_colon))
                    }
                    append(issue.timeStamp.millisToTime("dd/MM/yyyy"))
                },
                color = Dark,
                fontSize = 14.sp
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    ) {
                        append(stringResource(R.string.status_colon))
                    }
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Medium,
                            color = pillTextColor
                        )
                    ) {
                        append(
                            if (issue.resolved) {
                                stringResource(R.string.resolved)
                            } else {
                                stringResource(R.string.unresolved)
                            }
                        )
                    }
                },
                color = Dark,
                fontSize = 14.sp
            )
        }
    }
}