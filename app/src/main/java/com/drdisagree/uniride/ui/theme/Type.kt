package com.drdisagree.uniride.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.drdisagree.uniride.R

object AppFont {
    val NunitoFontFamily = FontFamily(
        Font(R.font.nunito_light, weight = FontWeight.W200),
        Font(R.font.nunito_regular, weight = FontWeight.W300),
        Font(R.font.nunito_medium, weight = FontWeight.W400),
        Font(R.font.nunito_semibold, weight = FontWeight.W500),
        Font(R.font.nunito_bold, weight = FontWeight.W600),
        Font(R.font.nunito_extrabold, weight = FontWeight.W700),
        Font(R.font.nunito_black, weight = FontWeight.W800),
        Font(R.font.nunito_light_italic, style = FontStyle.Italic, weight = FontWeight.W200),
        Font(R.font.nunito_italic, style = FontStyle.Italic, weight = FontWeight.W300),
        Font(R.font.nunito_medium_italic, style = FontStyle.Italic, weight = FontWeight.W400),
        Font(R.font.nunito_semibold_italic, style = FontStyle.Italic, weight = FontWeight.W500),
        Font(R.font.nunito_bold_italic, style = FontStyle.Italic, weight = FontWeight.W600),
        Font(R.font.nunito_extrabold_italic, style = FontStyle.Italic, weight = FontWeight.W700),
        Font(R.font.nunito_black_italic, style = FontStyle.Italic, weight = FontWeight.W800),
    )
}

// Set of Material typography styles to start with
private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.NunitoFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.NunitoFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.NunitoFontFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.NunitoFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.NunitoFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.NunitoFontFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.NunitoFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.NunitoFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.NunitoFontFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.NunitoFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.NunitoFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.NunitoFontFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.NunitoFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.NunitoFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.NunitoFontFamily)
)