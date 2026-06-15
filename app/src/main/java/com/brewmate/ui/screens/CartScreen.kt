package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewmate.model.CartItem
import com.brewmate.ui.theme.*
import com.brewmate.ui.util.pressScale
import com.brewmate.viewmodel.BrewViewModel

@Composable
fun CartScreen(viewModel: BrewViewModel, onNavigateToReceipt: () -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val tax by viewModel.cartTax.collectAsState()
    val total by viewModel.cartTotal.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel)))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "Cart",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (cartItems.isEmpty()) "0 items"
                    else "${cartItems.sumOf { it.quantity }} item(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        if (cartItems.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = LatteAccent,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No items in your cart",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Head to the menu to add some drinks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            //Cart items list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.id }) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onIncrement = {
                            viewModel.updateCartItemQuantity(cartItem.id, cartItem.quantity + 1)
                        },
                        onDecrement = {
                            viewModel.updateCartItemQuantity(cartItem.id, cartItem.quantity - 1)
                        },
                        onRemove = {
                            viewModel.removeFromCart(cartItem.id)
                        }
                    )
                }
            }

            // Order summary
            OrderSummaryCard(
                subtotal = subtotal,
                tax = tax,
                total = total,
                onPlaceOrder = { viewModel.placeOrder() }
            )
        }

        // Receipt dialog — shows automatically right after placing an order
        lastPlacedOrder?.let { entry ->
            ReceiptDialog(
                entry = entry,
                onDismiss = { viewModel.clearLastPlacedOrder() },
                onViewFullReceipt = onNavigateToReceipt
            )
        }
    }
}

// Cart item card

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Coffee,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Description + price
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkText,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", cartItem.unitPrice)} each",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Qty controls + total
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Decrement / remove
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (cartItem.quantity > 1) CoffeeBrown else LatteAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text(
                                "−",
                                color = if (cartItem.quantity > 1) Color.White else LightText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(CoffeeBrown),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$${String.format("%.2f", cartItem.totalPrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CoffeeBrown
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove",
                    tint = LightText
                )
            }
        }
    }
}

// Order summary card

@Composable
fun OrderSummaryCard(
    subtotal: Double,
    tax: Double,
    total: Double,
    onPlaceOrder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleLarge,
                color = DarkText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(label = "Sub Total", value = "$${String.format("%.2f", subtotal)}")
            Spacer(modifier = Modifier.height(6.dp))
            SummaryRow(label = "Tax (8%)", value = "$${String.format("%.2f", tax)}")

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = LatteAccent)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", total)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrown
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val placeOrderInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .pressScale(placeOrderInteraction),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                interactionSource = placeOrderInteraction
            ) {
                Text(
                    text = "Place Order",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Summary row

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = LightText)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = DarkText, fontWeight = FontWeight.SemiBold)
    }
}