package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewmate.model.MenuData
import com.brewmate.model.MenuItem
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder

@Composable
fun MenuScreen(viewModel: BrewViewModel, onItemClick: (String) -> Unit) {
    val favouriteIds by viewModel.favouriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        //Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel)))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "Coffee Menu",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap a coffee to customize",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(MenuData.items) { menuItem ->
                MenuItemCard(
                    menuItem = menuItem,
                    isFavourite = menuItem.id in favouriteIds,
                    onCardClick = {
                        viewModel.resetCustomization()
                        onItemClick(menuItem.id)
                    },
                    onFavouriteClick = {
                        viewModel.toggleFavourite(menuItem.id)
                    }
                )
            }
        }
    }
}

// Menu item card

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    isFavourite: Boolean,
    onCardClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Image placeholder with favourite overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel)))
            ) {
                Icon(
                    Icons.Outlined.Coffee,
                    contentDescription = menuItem.name,
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onFavouriteClick, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = if (isFavourite) Caramel else LightText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.titleLarge,
                color = DarkText,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Caramel,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = menuItem.rating.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = LightText
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = menuItem.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${String.format("%.2f", menuItem.basePrice)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeBrown
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tap to customize →",
                style = MaterialTheme.typography.labelSmall,
                color = LightText
            )
        }
    }
}