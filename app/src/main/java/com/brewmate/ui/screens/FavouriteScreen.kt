package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brewmate.model.MenuData
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel

@Composable
fun FavouriteScreen(viewModel: BrewViewModel, onItemClick: (String) -> Unit) {
    val favouriteIds by viewModel.favouriteIds.collectAsState()
    val favouriteItems = MenuData.items.filter { it.id in favouriteIds }

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
                    text = "Favourites",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (favouriteItems.isEmpty()) "No favourites yet"
                    else "${favouriteItems.size} saved item(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        if (favouriteItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = LatteAccent,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nothing favourited yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the heart on any menu item to save it here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
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
                items(favouriteItems, key = { it.id }) { menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        isFavourite = true,
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
}