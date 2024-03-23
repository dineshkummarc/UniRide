package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.drdisagree.uniride.ui.theme.Gray
import com.drdisagree.uniride.ui.theme.spacing

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StyledDropDownMenu(
    modifier: Modifier = Modifier,
    selectedText: String,
    itemList: Array<String> = emptyArray(),
    onItemSelected: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium1))
            .background(color = Color.White.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = Gray,
                shape = RoundedCornerShape(MaterialTheme.spacing.medium1)
            )
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
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
                    .background(color = Color.White)
            ) {
                itemList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                modifier = Modifier.padding(horizontal = 4.dp)
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