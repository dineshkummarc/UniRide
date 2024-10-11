package com.drdisagree.uniride.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.drdisagree.uniride.ui.theme.MediumSpacing
import com.drdisagree.uniride.ui.theme.Spacing
import java.util.Locale

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

fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    } catch (e: Exception) {
        Log.e("AppUtils", e.toString())
        Toast.makeText(
            context,
            "Unable to open browser",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun switchLanguage(context: Context) {
    context.findActivity()?.runOnUiThread {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager =
                context.getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales?.get(0)?.language
        } else {
            Locale.getDefault().language
        }

        val newLocale = if (currentLocale == null || currentLocale == "en") {
            "bn"
        } else {
            "en"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager =
                context.getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales = LocaleList(Locale(newLocale))
        } else {
            val locale = Locale(newLocale)
            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)

            context.createConfigurationContext(configuration)
        }
    }
}