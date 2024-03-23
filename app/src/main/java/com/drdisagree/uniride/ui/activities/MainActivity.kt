package com.drdisagree.uniride.ui.activities

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.theme.UniRideTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            UniRideTheme {
                val engine = rememberAnimatedNavHostEngine()
                val navController = engine.rememberNavController()

                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    startRoute = NavGraphs.root.startRoute,
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}