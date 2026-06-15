package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel
import java.util.Calendar



@Composable
fun HomeScreen(viewModel: BrewViewModel, onNavigateToMenu: () -> Unit) {
    val todaysOrderCount by viewModel.todaysOrderCount.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .verticalScroll(scrollState)
    ) {
        //Header
        HomeHeader()

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Today's Orders",
                value = todaysOrderCount.toString(),
                icon = Icons.Outlined.ListAlt,
                iconTint = CoffeeBrown
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "In Cart",
                value = cartCount.toString(),
                icon = Icons.Outlined.Coffee,
                iconTint = Caramel
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Coffee Card
        Text(
            text = "Featured",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = DarkText
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeaturedCoffeeCard(onOrderNow = onNavigateToMenu)

        Spacer(modifier = Modifier.height(24.dp))

        //Quick Actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = DarkText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                label = "New Order",
                icon = Icons.Outlined.Coffee,
                containerColor = CoffeeBrown,
                contentColor = Color.White,
                onClick = onNavigateToMenu
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                label = "View Menu",
                icon = Icons.Outlined.ListAlt,
                containerColor = LatteAccent,
                contentColor = DarkText,
                onClick = onNavigateToMenu
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        //Popular drinks strip
        Text(
            text = "Popular Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = DarkText
        )

        Spacer(modifier = Modifier.height(12.dp))

        PopularDrinksList(onNavigateToMenu)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Header composable

@Composable
fun HomeHeader() {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CoffeeBrown, Caramel)
                )
            )
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Column {
            Text(
                text = "$greeting,",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ready to brew something great?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

// Stat card

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = LightText)
            }
        }
    }
}

// Featured coffee card

@Composable
fun FeaturedCoffeeCard(onOrderNow: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(listOf(CoffeeBrown, Caramel))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Coffee,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Caramel Frappuccino",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Caramel,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("4.7", style = MaterialTheme.typography.bodyMedium, color = LightText)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "From \$4.50",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CoffeeBrown,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOrderNow,
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Order Now", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

// Quick action button

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = contentColor, fontWeight = FontWeight.SemiBold)
    }
}

// Popular drinks strip

@Composable
fun PopularDrinksList(onNavigateToMenu: () -> Unit) {
    val drinks = listOf(
        "Espresso" to "$2.50",
        "Iced Coffee" to "$3.25",
        "Hot Chocolate" to "$3.00"
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        drinks.forEach { (name, price) ->
            Card(
                onClick = onNavigateToMenu,
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LatteAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Coffee,
                            contentDescription = null,
                            tint = CoffeeBrown,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkText,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = price,
                        style = MaterialTheme.typography.bodyLarge,
                        color = CoffeeBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}