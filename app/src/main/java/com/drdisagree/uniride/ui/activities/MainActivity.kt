package com.drdisagree.uniride.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.drdisagree.uniride.data.utils.Constant.STUDENT_MAIL_SUFFIX
import com.drdisagree.uniride.ui.screens.NavGraphs
import com.drdisagree.uniride.ui.screens.destinations.HomeContainerDestination
import com.drdisagree.uniride.ui.theme.UniRideTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

                val isLoggedIn = Firebase.auth.currentUser != null
                val startRoute = if (!isLoggedIn) {
                    NavGraphs.root.startRoute
                } else {
                    val isStudent =
                        Firebase.auth.currentUser!!.email?.endsWith(STUDENT_MAIL_SUFFIX) == true

                    if (isStudent) {
                        HomeContainerDestination
                    } else {
                        NavGraphs.root.startRoute
                    }
                }

                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    startRoute = startRoute,
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}