package com.drdisagree.uniride.ui.screens.driver.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.drdisagree.uniride.R
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.WHICH_USER_COLLECTION
import com.drdisagree.uniride.data.utils.Prefs
import com.drdisagree.uniride.ui.components.transitions.FadeInOutTransition
import com.drdisagree.uniride.ui.components.views.TopAppBarNoButton
import com.drdisagree.uniride.ui.extension.Container
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination(style = FadeInOutTransition::class)
@Composable
fun DriverHome(
    navigator: DestinationsNavigator
) {
    Container(shadow = false) {
        Scaffold(
            topBar = {
                TopAppBarNoButton(
                    title = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.Cursive
                )
            },
            content = { paddingValues ->
                DriverHomeContent(
                    paddingValues = paddingValues,
                    navigator = navigator
                )
            }
        )
    }
}

@Composable
private fun DriverHomeContent(
    paddingValues: PaddingValues,
    navigator: DestinationsNavigator
) {
    Prefs.putString(WHICH_USER_COLLECTION, DRIVER_COLLECTION)

}