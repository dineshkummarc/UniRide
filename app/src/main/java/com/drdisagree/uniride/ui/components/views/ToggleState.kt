package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.ui.theme.Black
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.LightGray
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun ToggleState(
    modifier: Modifier = Modifier,
    states: List<String>,
    selectedOption: String,
    onSelectionChange: (String) -> Unit,
    fullWidth: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(MaterialTheme.spacing.medium1),
        modifier = if (fullWidth) {
            modifier.fillMaxWidth()
        } else {
            modifier.wrapContentSize()
        }.border(
            width = 1.dp,
            color = Gray,
            shape = RoundedCornerShape(MaterialTheme.spacing.medium1)
        )
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(MaterialTheme.spacing.medium1))
                .background(LightGray)
        ) {
            states.forEach { text ->
                val isSelected = text == selectedOption

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(
                            top = MaterialTheme.spacing.extraSmall2,
                            bottom = MaterialTheme.spacing.extraSmall2,
                            start = if (text == states.first()) MaterialTheme.spacing.extraSmall2 else MaterialTheme.spacing.extraSmall1,
                            end = if (text == states.last()) MaterialTheme.spacing.extraSmall2 else MaterialTheme.spacing.extraSmall1
                        )
                        .clip(shape = RoundedCornerShape(MaterialTheme.spacing.small3))
                        .clickable {
                            onSelectionChange(text)
                        }
                        .background(
                            if (isSelected) {
                                Black
                            } else {
                                LightGray
                            }
                        )
                        .padding(
                            vertical = MaterialTheme.spacing.small3,
                            horizontal = MaterialTheme.spacing.medium1
                        )
                        .then(if (fullWidth) Modifier.weight(1f) else Modifier)
                ) {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) {
                            Color.White
                        } else {
                            Black
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleStatePreview() {
    ToggleState(
        states = listOf("Option 1", "Option 2", "Option 3"),
        selectedOption = "Option 1",
        onSelectionChange = {}
    )
}