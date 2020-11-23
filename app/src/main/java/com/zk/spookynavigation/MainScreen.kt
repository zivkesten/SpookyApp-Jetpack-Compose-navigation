package com.zk.spookynavigation

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

const val ANIM_INT_ID_KEY = "scaryAnimationId"

sealed class BottomNavigationScreens(val route: String,
                                     @StringRes val resourceId: Int,
                                     val icon: VectorAsset,
                                     val scaryAnimation: ScaryAnimation) {
    object Frankendroid : BottomNavigationScreens(
            "Frankendroid",
            R.string.frankendroid_route,
            Icons.Filled.Terrain,
            ScaryAnimation.Frankendroid)
    object Pumpkin : BottomNavigationScreens(
            "Pumpkin",
            R.string.pumpkin_screen_route,
            Icons.Filled.FoodBank,
            ScaryAnimation.Pumpkin)
    object Ghost : BottomNavigationScreens(
            "Ghost",
            R.string.ghost_screen_route,
            Icons.Filled.Fireplace,
            ScaryAnimation.Ghost)
    object ScaryBag : BottomNavigationScreens(
            "ScaryBag",
            R.string.scary_bag_screen_route,
            Icons.Filled.Cake,
            ScaryAnimation.ScaryBag)
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
        bottomBar = {
            SpookyAppBottomNavigation(navController, bottomNavigationItems)
        },
    ) {
        MainScreenNavigationConfigurations(navController)
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController, startDestination = BottomNavigationScreens.Frankendroid.route) {
        //This destination does not take any arguments
        composable(BottomNavigationScreens.Frankendroid.route) {
            ScaryScreen(ScaryAnimation.Frankendroid.animId)
        }
        //This destination takes a String argument
        composable(BottomNavigationScreens.Pumpkin.route
            .plus("/{$ANIM_INT_ID_KEY}")) {
            ScaryScreen(ScaryAnimation.Ghost.animId)
        }
        //This destination takes an Integer argument
        composable(BottomNavigationScreens.Ghost.route
            .plus("/{$ANIM_INT_ID_KEY}"),
                arguments = listOf(navArgument(ANIM_INT_ID_KEY) {
                    type = NavType.IntType
                })
        ) { backStackEntry ->
            ScaryScreen(backStackEntry.arguments?.getInt(ANIM_INT_ID_KEY))
        }
        //This destination takes an optional string argument
        composable(
            BottomNavigationScreens.ScaryBag.route
                .plus("?$ANIM_INT_ID_KEY={$ANIM_INT_ID_KEY}"),
            arguments = listOf(navArgument(ANIM_INT_ID_KEY) {
                defaultValue = ScaryAnimation.ScaryBag.animId.toString()
            })
        ) { backStackEntry ->
            val scaryAnimationIdAsString = backStackEntry.arguments?.getString(ANIM_INT_ID_KEY)
            ScaryScreen(null, scaryAnimationIdAsString)
        }
    }
}

@Composable
fun ScaryScreen(scaryAnimationId: Int?, scaryAnimationIdAsString: String? = null) {
    //Generate animation from an integer type animationId
    scaryAnimationId?.let { animationId ->
        Animation(animationId)
    }

    //Generate animation from a string type animationId
    scaryAnimationIdAsString?.let { animationIdAsString ->
        Animation(animationIdAsString.toInt())
    }
}

@Composable
private fun Animation(
    animationId: Int
) {
    val context = ContextAmbient.current
    val customView = remember { LottieAnimationView(context) }
    // Adds view to Compose
    AndroidView({ customView },
        modifier = Modifier.background(Color.Black)
    ) { view ->
        // View's been inflated - add logic here if necessary
        with(view) {
            setAnimation(animationId)
            playAnimation()
            repeatMode = LottieDrawable.REVERSE
        }
    }
}

@Composable
private fun SpookyAppBottomNavigation(
    navController: NavHostController,
    items: List<BottomNavigationScreens>
) {
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(

                icon = { Icon(screen.icon) },
                label = { Text(stringResource(id = screen.resourceId)) },
                selected = currentRoute == screen.route,
                alwaysShowLabels = false, // This hides the title for the unselected items
                onClick = {
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination

                    if (currentRoute != screen.route) {

                        when(screen) {
                            // On the first screen we navigate with no arguments
                            is BottomNavigationScreens.Frankendroid -> {
                                navigateWithArguments(
                                    screen = screen,
                                    navController = navController)
                            }
                            // On the second and third screen we navigate with the
                            // animation id integer as the argument
                            is BottomNavigationScreens.Pumpkin,
                               BottomNavigationScreens.Ghost -> {
                                navigateWithArguments(
                                    "/${screen.scaryAnimation.animId}",
                                    screen,
                                    navController)
                            }
                            // On the fourth screen we navigate with an optional type argument
                            // Of the animation id integer
                            is BottomNavigationScreens.ScaryBag -> {
                                navigateWithArguments(
                                    "?$ANIM_INT_ID_KEY=${ScaryAnimation.ScaryBag.animId}",
                                    screen,
                                    navController)
                            }
                        }
                    }
                }
            )
        }
    }
}

private fun navigateWithArguments(
    argument: String? = null,
    screen: BottomNavigationScreens,
    navController: NavHostController
) {
    var route = screen.route
    // If argument is supplied, navigate using that argument
    argument?.let {
        route = screen.route.plus(it)
    }
    navController.navigate(route)
}

// In this version of the navigation implementation we had to hack a workaround to
// obtain the current route.
// The reason for that is that we are changing the route by contacting an argument to it
// And so here we would remove the argument from the route.
@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Here we can extract the original route from the backstack by removing the arguments from it
    // The arguments would be added after a "/" character or a "?" character
    val routeWithArgument = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
    // If the route with the argument was "Pumpkin/{12345}"
    // Then the listOfStringsSplit would be ["Pumpkin", "12345"]
    val listOfStringsSplit = routeWithArgument?.split("/","?")
    //So we would return the first string, which would be the original route
    return listOfStringsSplit?.get(0)
}