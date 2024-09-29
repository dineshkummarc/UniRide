package com.drdisagree.uniride.ui.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.ui.theme.NoRippleTheme
import com.drdisagree.uniride.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledRadioButton(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int? = null,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        options.forEachIndexed { index, text ->
            CompositionLocalProvider(LocalRippleConfiguration provides NoRippleTheme) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (index == selectedIndex),
                            onClick = {
                                onOptionSelected(index)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        modifier = Modifier.size(32.dp),
                        selected = (index == selectedIndex),
                        onClick = { onOptionSelected(index) }
                    )
                    Text(
                        text = text,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = MaterialTheme.spacing.small2)
                    )
                }
            }
        }
    }
}