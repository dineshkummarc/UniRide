package com.drdisagree.uniride.ui.screens.student.more.chatbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.ChatMessage
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Blue
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun ChatBox(
    navigator: DestinationsNavigator,
    student: Student
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.chat_box_title),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                ChatBoxContent(
                    paddingValues = paddingValues,
                    navigator = navigator,
                    student = student
                )
            }
        )
    }
}

@Composable
private fun ChatBoxContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator,
    student: Student,
    chatBoxViewModel: ChatBoxViewModel = hiltViewModel()
) {
    val messagesState by chatBoxViewModel.messagesState.collectAsState()
    val messageSendingState by chatBoxViewModel.messageSendingState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium1)
    ) {
        when (messagesState) {
            is Resource.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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

            is Resource.Success -> {
                val messages = (messagesState as Resource.Success<List<ChatMessage>>).data

                if (messages.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = LightGray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text(
                            text = stringResource(R.string.no_messages),
                            color = DarkGray.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        reverseLayout = true
                    ) {
                        itemsIndexed(
                            messages,
                            key = { _, chatMessage -> chatMessage.timeStamp }) { _, chatMessage ->
                            ChatBubble(chatMessage = chatMessage, currentUserId = student.userId)
                        }
                    }
                }
            }

            is Resource.Error -> {
                val errorMessage = (messagesState as Resource.Error).message

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage ?: stringResource(R.string.an_error_occurred)
                    )
                }
            }

            else -> {}
        }

        var messageText by remember { mutableStateOf("") }
        val onSendMessage: () -> Unit = {
            if (messageText.trim().isNotBlank()) {
                chatBoxViewModel.sendMessage(student, messageText.trim())
                messageText = ""
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .border(
                        BorderStroke(width = 2.dp, color = LightGray),
                        shape = RoundedCornerShape(50)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(color = Black),
                placeholder = {
                    Text(
                        text = stringResource(R.string.type_a_message),
                        color = DarkGray.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSendMessage() }
                )
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small2))

            Button(
                onClick = { onSendMessage() },
                modifier = Modifier.height(56.dp),
                enabled = messageSendingState !is Resource.Loading
            ) {
                when (messageSendingState) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(chatMessage: ChatMessage, currentUserId: String, modifier: Modifier = Modifier) {
    var isTimestampVisible by remember { mutableStateOf(false) }
    val isOwnChat by remember { mutableStateOf(chatMessage.sender.userId == currentUserId) }

    val boxAlignment = if (isOwnChat) {
        Arrangement.End
    } else {
        Arrangement.Start
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.spacing.small2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = boxAlignment
        ) {
            Spacer(modifier = Modifier.weight(if (isOwnChat) 1f else 0.001f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentWidth(),
                horizontalArrangement = boxAlignment
            ) {
                Spacer(modifier = Modifier.weight(if (isOwnChat) 1f else 0.001f))
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                ) {
                    Surface(
                        modifier = Modifier.wrapContentWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = if (isOwnChat) {
                            Blue
                        } else {
                            LightGray
                        },
                        onClick = {
                            isTimestampVisible = !isTimestampVisible
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    vertical = MaterialTheme.spacing.small2,
                                    horizontal = MaterialTheme.spacing.medium1
                                )
                        ) {
                            if (!isOwnChat) {
                                Text(
                                    text = chatMessage.sender.userName
                                        ?: chatMessage.sender.email
                                        ?: chatMessage.sender.userId,
                                    color = Black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = chatMessage.message,
                                color = if (isOwnChat) {
                                    Color.White
                                } else {
                                    Black
                                }.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(if (isOwnChat) 0.001f else 1f))
            }
            Spacer(modifier = Modifier.weight(if (isOwnChat) 0.001f else 1f))
        }
        AnimatedVisibility(
            visible = isTimestampVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = formatTimeAgo(chatMessage.timeStamp),
                color = Black.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = if (isOwnChat) {
                    TextAlign.End
                } else {
                    TextAlign.Start
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.small1)
            )
        }
    }
}

private fun formatTimeAgo(millis: Long): String {
    val now = System.currentTimeMillis()
    val seconds = (now - millis) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val years = days / 365

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
        hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
        days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
        weeks < 52 -> "$weeks week${if (weeks != 1L) "s" else ""} ago"
        else -> "$years year${if (years != 1L) "s" else ""} ago"
    }
}
