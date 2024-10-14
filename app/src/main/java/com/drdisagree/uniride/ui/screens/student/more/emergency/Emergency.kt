package com.drdisagree.uniride.ui.screens.student.more.emergency

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.utils.Constant.EMERGENCY_PHONE_NUMBERS
import com.drdisagree.uniride.ui.components.navigation.MoreNavGraph
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.Container
import com.drdisagree.uniride.ui.components.views.SwipeButton
import com.drdisagree.uniride.ui.components.views.TopAppBarWithBackButton
import com.drdisagree.uniride.ui.theme.DarkGray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@MoreNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun Emergency(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarWithBackButton(
                    title = stringResource(id = R.string.emergency_title),
                    onBackClick = {
                        navigator.navigateUp()
                    }
                )
            },
            content = { paddingValues ->
                EmergencyContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun EmergencyContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium1),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(LightGray)
                .padding(MaterialTheme.spacing.medium1)
        ) {
            Text(
                text = stringResource(R.string.emergency_description),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.End),
                tint = DarkGray,
                imageVector = Icons.Default.FormatQuote,
                contentDescription = "Quote end",
            )
        }

        Text(
            text = stringResource(R.string.for_emergency_call),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large1)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium1))

        EMERGENCY_PHONE_NUMBERS.forEach { (number, name) ->
            val isCompleteState = remember { mutableStateOf(false) }
            val callPhoneLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    makeCall(context, number)
                } else {
                    Toast.makeText(
                        context,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            SwipeButton(
                text = name,
                isCompleteState = isCompleteState,
                backgroundColor = Color(0xffce2029),
                onSwipe = {
                    coroutineScope.launch {
                        delay(2000)
                        isCompleteState.value = true
                    }
                }
            )

            if (isCompleteState.value) {
                callPhoneLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }
}

private fun makeCall(context: Context, phoneNumber: String) {
    context.startActivity(
        Intent(Intent.ACTION_CALL).apply {
            setData(Uri.parse("tel:$phoneNumber"))
        }
    )
}