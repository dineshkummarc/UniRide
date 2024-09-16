package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StyledDropDownMenu(
    modifier: Modifier = Modifier,
    selectedText: String,
    itemList: Array<String> = emptyArray(),
    onItemSelected: ((String) -> Unit)? = null,
    fillMaxWidth: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .defaultMinSize(minHeight = 64.dp)
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium1))
            .background(color = Color.White.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = Gray,
                shape = RoundedCornerShape(MaterialTheme.spacing.medium1)
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .then(
                        if (fillMaxWidth) Modifier
                            .padding(
                                top = 3.dp,
                                start = MaterialTheme.spacing.small1,
                                end = MaterialTheme.spacing.small1
                            )
                            .fillMaxWidth() else Modifier
                    )
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable),
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                visualTransformation = ellipsisVisualTransformation()
            )
        }

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(surface = Color.White),
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(MaterialTheme.spacing.medium1))
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .then(if (fillMaxWidth) Modifier.exposedDropdownSize() else Modifier)
                    .background(color = Color.White),
                containerColor = Color.White
            ) {
                itemList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small3)
                            )
                        },
                        onClick = {
                            expanded = false
                            onItemSelected?.invoke(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ellipsisVisualTransformation(maxLength: Int = 30) = VisualTransformation { text ->
    val ellipsis = "..."
    val originalText = text.text

    if (originalText.length <= maxLength) {
        TransformedText(text = text, offsetMapping = OffsetMapping.Identity)
    } else {
        val truncatedText = originalText.take(maxLength - ellipsis.length) + ellipsis
        val transformedAnnotatedString = AnnotatedString(truncatedText)

        val originalToTransformed =
            IntArray(originalText.length) { it.coerceAtMost(maxLength - ellipsis.length) }
        val transformedToOriginal = IntArray(truncatedText.length) {
            if (it < maxLength - ellipsis.length) it else maxLength - ellipsis.length
        }

        TransformedText(
            transformedAnnotatedString,
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return originalToTransformed.getOrElse(offset) { truncatedText.length }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return transformedToOriginal.getOrElse(offset) { originalText.length }
                }
            }
        )
    }
}
