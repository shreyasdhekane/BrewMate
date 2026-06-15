package com.brewmate.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brewmate.ui.screens.*
import com.brewmate.viewmodel.BrewViewModel

private fun isDetailRoute(route: String?): Boolean {
    if (route == null) return false
    return route.startsWith("product_detail") || route == Screen.OrderHistory.route || route == Screen.Receipt.route
}

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: BrewViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            if (isDetailRoute(targetState.destination.route)) {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(280)
                ) + fadeIn(animationSpec = tween(200))
            } else {
                fadeIn(animationSpec = tween(220))
            }
        },
        exitTransition = {
            if (isDetailRoute(targetState.destination.route)) {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(150))
            } else {
                fadeOut(animationSpec = tween(180))
            }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220))
        },
        popExitTransition = {
            if (isDetailRoute(initialState.destination.route)) {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(150))
            } else {
                fadeOut(animationSpec = tween(180))
            }
        }
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                viewModel = viewModel,
                onItemClick = { menuItemId ->
                    navController.navigate(Screen.ProductDetail.createRoute(menuItemId))
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("menuItemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: ""
            ProductDetailScreen(
                viewModel = viewModel,
                menuItemId = menuItemId,
                onAddToCart = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                viewModel = viewModel,
                onNavigateToReceipt = {
                    navController.navigate(Screen.Receipt.route)
                }
            )
        }

        composable(Screen.Favourite.route) {
            FavouriteScreen(
                viewModel = viewModel,
                onItemClick = { menuItemId ->
                    navController.navigate(Screen.ProductDetail.createRoute(menuItemId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onOrderHistoryClick = {
                    navController.navigate(Screen.OrderHistory.route)
                }
            )
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Receipt.route) {
            ReceiptScreen(
                viewModel = viewModel,
                onDone = {
                    viewModel.clearLastPlacedOrder()
                    navController.popBackStack()
                }
            )
        }
    }
}