package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewmate.model.OrderHistoryEntry
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(viewModel: BrewViewModel, onBack: () -> Unit) {
    val orderHistory by viewModel.orderHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeBrown)
            )
        },
        containerColor = WarmCream
    ) { innerPadding ->
        if (orderHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ListAlt,
                        contentDescription = null,
                        tint = LatteAccent,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No orders yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Placed orders will show up here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orderHistory, key = { it.orderId }) { entry ->
                    OrderHistoryCard(entry = entry)
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(entry: OrderHistoryEntry) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }

    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${entry.items.sumOf { it.quantity }} item(s)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "$${String.format("%.2f", entry.total)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrown
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = LatteAccent)
                Spacer(modifier = Modifier.height(12.dp))

                entry.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}× ${item.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format("%.2f", item.unitPrice * item.quantity)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = LatteAccent)
                Spacer(modifier = Modifier.height(8.dp))

                SummaryRow(label = "Sub Total", value = "$${String.format("%.2f", entry.subtotal)}")
                Spacer(modifier = Modifier.height(4.dp))
                SummaryRow(label = "Tax", value = "$${String.format("%.2f", entry.tax)}")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (expanded) "Tap to collapse" else "Tap to view details",
                style = MaterialTheme.typography.labelSmall,
                color = LightText
            )
        }
    }
}