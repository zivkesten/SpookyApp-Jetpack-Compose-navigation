package com.zk.navigationtest

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChargingStation
import androidx.compose.material.icons.filled.LocalSee
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int) {
    object Frankendroid : BottomNavigationScreens("Frankendroid", R.string.frankendroid_route)
    object Pumpkin : BottomNavigationScreens("Pumpkin", R.string.pumpkin_screen_route)
    object Ghost : BottomNavigationScreens("Ghost", R.string.ghost_screen_route)
    object ScaryBag : BottomNavigationScreens("ScaryBag", R.string.scary_bag_screen_route)
}

sealed class ScaryAnimation(val animId: Int){
    object Frankendroid: ScaryAnimation(R.raw.frankensteindroid)
    object Pumpkin: ScaryAnimation(R.raw.jackolantern)
    object Ghost: ScaryAnimation(R.raw.ghost)
    object ScaryBag: ScaryAnimation(R.raw.bag)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        BottomNavigationScreens.Frankendroid,
        BottomNavigationScreens.Pumpkin,
        BottomNavigationScreens.Ghost,
        BottomNavigationScreens.ScaryBag
    )
    Scaffold(
        bottomBar = { TrackShowsBottomNavigation(navController, bottomNavigationItems) },
    ) {
        MainScreenNavigationConfigurations(navController)
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController, startDestination = BottomNavigationScreens.Frankendroid.route) {
        composable(BottomNavigationScreens.Frankendroid.route) {
            CustomView(ScaryAnimation.Frankendroid)
        }
        composable(BottomNavigationScreens.Pumpkin.route) {
            CustomView(ScaryAnimation.Pumpkin)
        }
        composable(BottomNavigationScreens.Ghost.route) {
            CustomView(ScaryAnimation.Ghost)
        }
        composable(BottomNavigationScreens.ScaryBag.route) {
            CustomView(ScaryAnimation.ScaryBag)
        }
    }
}

@Composable
fun CustomView(scaryAnimation: ScaryAnimation) {
    val context = ContextAmbient.current
    val customView = remember { LottieAnimationView(context) }
    // Adds view to Compose
    AndroidView({ customView }) { view ->
        // View's been inflated - add logic here if necessary
        with(view) {
            setAnimation(scaryAnimation.animId)
            playAnimation()
            repeatMode = LottieDrawable.REVERSE
        }
    }
}

@Composable
private fun TrackShowsBottomNavigation(
    navController: NavHostController,
    items: List<BottomNavigationScreens>
) {
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    when (screen) {
                        is BottomNavigationScreens.Frankendroid -> Icon(Icons.Filled.Terrain)
                        is BottomNavigationScreens.Pumpkin -> Icon(Icons.Filled.Satellite)
                        is BottomNavigationScreens.Ghost -> Icon(Icons.Filled.LocalSee)
                        is BottomNavigationScreens.ScaryBag -> Icon(Icons.Filled.ChargingStation)
                    }
                },
                label = { Text(stringResource(id = screen.resourceId)) },
                selected = currentRoute == screen.route,
                alwaysShowLabels = false,
                onClick = {
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}