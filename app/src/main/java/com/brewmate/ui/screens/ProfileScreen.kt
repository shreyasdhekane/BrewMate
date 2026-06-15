package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Favorite
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
import com.brewmate.model.BaristaRole
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel

@Composable
fun ProfileScreen(viewModel: BrewViewModel, onOrderHistoryClick: () -> Unit) {
    val todaysOrderCount by viewModel.todaysOrderCount.collectAsState()
    val favouriteIds by viewModel.favouriteIds.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val currentBarista by viewModel.currentBarista.collectAsState()

    val displayName = currentBarista?.name ?: "Guest"
    val shiftInfo = currentBarista?.shiftInfo ?: "Guest Access"
    val role = currentBarista?.role ?: BaristaRole.GUEST
    val idLabel = currentBarista?.id

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // ── Header with avatar ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel)))
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                if (role != BaristaRole.GUEST) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ID: $idLabel" + if (role == BaristaRole.ADMIN) " • Admin" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = shiftInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Stats grid ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                label = "Today's Orders",
                value = todaysOrderCount.toString(),
                icon = Icons.Outlined.ListAlt,
                iconTint = CoffeeBrown
            )
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                label = "Favourites",
                value = favouriteIds.size.toString(),
                icon = Icons.Outlined.Favorite,
                iconTint = Caramel
            )
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                label = "In Cart",
                value = cartCount.toString(),
                icon = Icons.Outlined.Coffee,
                iconTint = LightText
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Settings list ────────────────────────────────────────
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileMenuRow(label = "Shift Details", icon = Icons.Filled.AccessTime)
            ProfileMenuRow(label = "Order History", icon = Icons.Outlined.ListAlt, onClick = onOrderHistoryClick)
            ProfileMenuRow(
                label = "Log Out",
                icon = Icons.Filled.Logout,
                tint = Color(0xFFB3261E),
                onClick = { viewModel.logOut() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Profile stat card ─────────────────────────────────────────────────────────

@Composable
fun ProfileStatCard(
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
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = LightText,
                maxLines = 1
            )
        }
    }
}

// ── Settings row ──────────────────────────────────────────────────────────────

@Composable
fun ProfileMenuRow(
    label: String,
    icon: ImageVector,
    tint: Color = CoffeeBrown,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (tint == CoffeeBrown) DarkText else tint,
                modifier = Modifier.weight(1f)
            )
        }
    }
}