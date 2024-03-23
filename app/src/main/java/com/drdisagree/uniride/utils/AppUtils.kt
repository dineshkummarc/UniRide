package com.drdisagree.uniride.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.drdisagree.uniride.ui.theme.MediumSpacing
import com.drdisagree.uniride.ui.theme.Spacing

@Composable
fun AppUtils(
    spacing: Spacing, content: @Composable () -> Unit
) {
    val appDimen by remember {
        mutableStateOf(spacing)
    }

    CompositionLocalProvider(value = LocalAppSpacing.provides(spacing)) {
        content()
    }
}

val LocalAppSpacing = compositionLocalOf {
    MediumSpacing
}

val ScreenOrientation @Composable get() = LocalConfiguration.current.orientation

fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
}