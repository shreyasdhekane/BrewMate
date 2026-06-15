package com.brewmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brewmate.ui.navigation.AppNavGraph
import com.brewmate.ui.navigation.Screen
import com.brewmate.ui.navigation.bottomNavItems
import com.brewmate.ui.screens.AuthScreen
import com.brewmate.ui.theme.BrewMateTheme
import com.brewmate.ui.theme.CoffeeBrown
import com.brewmate.ui.theme.WarmCream
import com.brewmate.viewmodel.BrewViewModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewMateTheme {
                val viewModel: BrewViewModel = viewModel()
                BrewMateRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BrewMateRoot(viewModel: BrewViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentBarista by viewModel.currentBarista.collectAsState()

    when {
        isLoading -> LoadingScreen()
        currentBarista == null -> AuthScreen(viewModel = viewModel)
        else -> BrewMateApp(viewModel = viewModel)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = CoffeeBrown)
    }
}

@Composable
fun BrewMateApp(viewModel: BrewViewModel) {
    val navController = rememberNavController()

    Surface {
        Column {
            Scaffold(
                bottomBar = {
                    BrewBottomNavBar(navController = navController, viewModel = viewModel)
                }
            ) { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    AppNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BrewBottomNavBar(navController: androidx.navigation.NavHostController, viewModel: BrewViewModel) {
    val cartCount by viewModel.cartItemCount.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (item.screen == Screen.Cart && cartCount > 0) {
                        BadgedBox(badge = { AnimatedCartBadge(count = cartCount) }) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun AnimatedCartBadge(count: Int) {
    var previousCount by remember { mutableStateOf(count) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(count) {
        if (count != previousCount && count > 0) {
            scale.animateTo(1.4f, animationSpec = tween(100))
            scale.animateTo(1f, animationSpec = tween(150))
        }
        previousCount = count
    }

    Badge(
        modifier = Modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
    ) {
        Text(count.toString())
    }
}