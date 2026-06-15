package com.brewmate.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object Favourite : Screen("favourite")
    object Profile : Screen("profile")

    object Receipt : Screen("receipt")
    object ProductDetail : Screen("product_detail/{menuItemId}") {
        fun createRoute(menuItemId: String) = "product_detail/$menuItemId"
    }
    object OrderHistory : Screen("order_history")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Menu, "Menu", Icons.Outlined.Coffee),
    BottomNavItem(Screen.Cart, "Cart", Icons.Filled.ShoppingCart),
    BottomNavItem(Screen.Favourite, "Favourite", Icons.Filled.Favorite),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person)
)