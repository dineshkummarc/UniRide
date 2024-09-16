package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun LoadingDialog(
    text: String = "Please wait",
    dialogProperties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialogProperties
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .shadow(
                    elevation = MaterialTheme.spacing.medium1,
                    shape = RoundedCornerShape(MaterialTheme.spacing.medium2)
                )
                .background(
                    Color.White,
                    shape = RoundedCornerShape(MaterialTheme.spacing.medium2)
                )
                .padding(
                    start = MaterialTheme.spacing.large2,
                    top = MaterialTheme.spacing.medium3,
                    end = MaterialTheme.spacing.large2,
                    bottom = MaterialTheme.spacing.medium2
                )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small2)
                    .widthIn(max = 120.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}