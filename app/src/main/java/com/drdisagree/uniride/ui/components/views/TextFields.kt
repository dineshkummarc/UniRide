package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing

@Composable
fun StyledTextField(
    modifier: Modifier = Modifier,
    inputText: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    leadingIconOnClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    trailingIconOnClick: (() -> Unit)? = null,
    maxCharacter: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorIconOnClick: (() -> Unit)? = null,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE
) {
    BasicTextField(
        modifier = modifier
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium1))
            .background(color = Color.White.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = Gray,
                shape = RoundedCornerShape(MaterialTheme.spacing.medium1)
            )
            .padding(horizontal = MaterialTheme.spacing.small2),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        value = inputText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onValueChange = {
            if (it.length <= maxCharacter) {
                onValueChange(it)
            }
        },
        textStyle = TextStyle(
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 16.sp
        ),
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 14.dp),
                content = {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(MaterialTheme.spacing.medium3)
                                .let { if (leadingIconOnClick != null) it.clickable { leadingIconOnClick() } else it }
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopStart,
                        content = {
                            if (inputText.isEmpty()) {
                                Text(
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = placeholder,
                                    color = Color.Black.copy(alpha = 0.4f)
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (trailingIcon != null) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(MaterialTheme.spacing.small2))
                                .let { if (trailingIconOnClick != null) it.clickable { trailingIconOnClick() } else it }
                                .padding(6.dp)
                        )
                    }

                    if (isError) {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = null,
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(MaterialTheme.spacing.small2))
                                .let { if (errorIconOnClick != null) it.clickable { errorIconOnClick() } else it }
                                .padding(6.dp)
                        )
                    }
                }
            )
        }
    )
}