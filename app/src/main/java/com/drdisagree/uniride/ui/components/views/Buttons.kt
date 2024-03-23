package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.SemiBlack
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun ButtonPrimary(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    ButtonBase(
        modifier = modifier
            .getButtonModifier(),
        text = text,
        colors = ButtonDefaults.buttonColors(
            containerColor = SemiBlack,
            contentColor = Color.White
        ),
        onClick = onClick
    )
}

@Composable
fun ButtonSecondary(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    ButtonBase(
        modifier = modifier
            .getButtonModifier(),
        text = text,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gray,
            contentColor = Color.Black
        ),
        onClick = onClick
    )
}

@Composable
private fun ButtonBase(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .getButtonModifier(),
        onClick = {
            onClick()
        },
        colors = colors,
        shape = RoundedCornerShape(MaterialTheme.spacing.medium1),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            focusedElevation = 8.dp,
            hoveredElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun Modifier.getButtonModifier() = this.then(
    Modifier
        .height(MaterialTheme.spacing.fieldHeight)
)