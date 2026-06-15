package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.brewmate.model.AddOnType
import com.brewmate.model.MenuData
import com.brewmate.model.MilkType
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloatAsState
import com.brewmate.ui.util.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: BrewViewModel,
    menuItemId: String,
    onAddToCart: () -> Unit,
    onBack: () -> Unit
) {
    val menuItem = MenuData.items.find { it.id == menuItemId } ?: return

    val selectedMilk by viewModel.selectedMilkType.collectAsState()
    val selectedAddOns by viewModel.selectedAddOns.collectAsState()
    val quantity by viewModel.selectedQuantity.collectAsState()

    // Live price — recomputes every time milk or add-ons change
    val liveCoffee by remember(selectedMilk, selectedAddOns) {
        derivedStateOf { viewModel.buildCustomizedCoffee(menuItem) }
    }
    val livePrice = liveCoffee.getCost() * quantity
    val liveDescription = liveCoffee.getDescription()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(menuItem.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeBrown)
            )
        },
        bottomBar = {
            AddToCartBar(
                price = livePrice,
                quantity = quantity,
                onDecrement = { viewModel.decrementQuantity() },
                onIncrement = { viewModel.incrementQuantity() },
                onAddToCart = {
                    viewModel.addCurrentSelectionToCart(menuItem)
                    onAddToCart()
                }
            )
        },
        containerColor = WarmCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Coffee image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.verticalGradient(listOf(CoffeeBrown, Caramel))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Coffee,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Name + description
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DarkText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = liveDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", livePrice)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrown
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = LatteAccent)
            Spacer(modifier = Modifier.height(20.dp))

            // Milk type
            SectionHeader("Milk Type")
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Two chips per row
                MilkType.entries.chunked(2).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { milkType ->
                            MilkChip(
                                milkType = milkType,
                                selected = selectedMilk == milkType,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.selectMilkType(milkType) }
                            )
                        }
                        // Fill empty slot if odd number
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = LatteAccent)
            Spacer(modifier = Modifier.height(20.dp))

            //  Add-ons
            SectionHeader("Add-ons")
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AddOnType.entries.chunked(2).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { addOn ->
                            AddOnChip(
                                addOn = addOn,
                                selected = addOn in selectedAddOns,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.toggleAddOn(addOn) }
                            )
                        }
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Section header

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = DarkText,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

// Milk chip (single-select)

@Composable
fun MilkChip(
    milkType: MilkType,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val extraCostLabel = if (milkType.extraCost > 0) " +$${String.format("%.2f", milkType.extraCost)}" else ""

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) CoffeeBrown else Color.White,
        label = "milkChipBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) CoffeeBrown else LatteAccent,
        label = "milkChipBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "milkChipScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = milkType.label + extraCostLabel,
                color = if (selected) Color.White else DarkText,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

// Add-on chip (multi-select)

@Composable
fun AddOnChip(
    addOn: AddOnType,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cost = when (addOn) {
        AddOnType.VANILLA_SYRUP -> 0.60
        AddOnType.EXTRA_SHOT -> 1.00
        AddOnType.WHIP_CREAM -> 0.75
        AddOnType.CARAMEL_DRIZZLE -> 0.70
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Caramel else Color.White,
        label = "addOnChipBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) Caramel else LatteAccent,
        label = "addOnChipBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "addOnChipScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = DarkText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "${addOn.label} +$${"%.2f".format(cost)}",
                color = DarkText,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

//Bottom Add to Cart bar

@Composable
fun AddToCartBar(
    price: Double,
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Qty stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (quantity > 1) CoffeeBrown else LatteAccent),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(34.dp)) {
                        Text(
                            "−",
                            color = if (quantity > 1) Color.White else LightText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Text(
                    text = quantity.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CoffeeBrown),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onIncrement, modifier = Modifier.size(34.dp)) {
                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }

            // Add to cart button
            val addToCartInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = onAddToCart,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                interactionSource = addToCartInteraction,
                modifier = Modifier
                    .height(48.dp)
                    .pressScale(addToCartInteraction)
            ) {
                Text(
                    text = "Add to Cart • $${String.format("%.2f", price)}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}