package com.brewmate.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.brewmate.model.OrderHistoryEntry
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Mock Print Button

private enum class PrintState { IDLE, PRINTING, PRINTED }

@Composable
fun PrintReceiptButton(modifier: Modifier = Modifier) {
    var printState by remember { mutableStateOf(PrintState.IDLE) }

    LaunchedEffect(printState) {
        when (printState) {
            PrintState.PRINTING -> {
                delay(1200)
                printState = PrintState.PRINTED
            }
            PrintState.PRINTED -> {
                delay(1500)
                printState = PrintState.IDLE
            }
            else -> {}
        }
    }

    OutlinedButton(
        onClick = { if (printState == PrintState.IDLE) printState = PrintState.PRINTING },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, CoffeeBrown),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoffeeBrown)
    ) {
        when (printState) {
            PrintState.IDLE -> {
                Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Print Receipt", fontWeight = FontWeight.SemiBold)
            }
            PrintState.PRINTING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = CoffeeBrown
                )
                Spacer(Modifier.width(8.dp))
                Text("Printing...", fontWeight = FontWeight.SemiBold)
            }
            PrintState.PRINTED -> {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Printed!", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

//Quick Receipt Dialog

@Composable
fun ReceiptDialog(
    entry: OrderHistoryEntry,
    onDismiss: () -> Unit,
    onViewFullReceipt: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CoffeeBrown.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = CoffeeBrown,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Order Placed!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DarkText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText
                )

                Spacer(Modifier.height(16.dp))
                Divider(color = LatteAccent)
                Spacer(Modifier.height(16.dp))

                val itemsToShow = entry.items.take(3)
                itemsToShow.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${item.quantity}× ${item.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "$${String.format("%.2f", item.unitPrice * item.quantity)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
                if (entry.items.size > 3) {
                    Text(
                        "+${entry.items.size - 3} more item(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = LightText
                    )
                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(8.dp))
                Divider(color = LatteAccent)
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL", style = MaterialTheme.typography.titleLarge, color = DarkText, fontWeight = FontWeight.Bold)
                    Text(
                        "$${String.format("%.2f", entry.total)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoffeeBrown
                    )
                }

                Spacer(Modifier.height(20.dp))

                PrintReceiptButton(modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onViewFullReceipt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, LatteAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText)
                ) {
                    Text("View Full Receipt", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown)
                ) {
                    Text("New Order", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

//Full Receipt Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(viewModel: BrewViewModel, onDone: () -> Unit) {
    // Snapshot once — survives clearing lastPlacedOrder when "Done" is pressed
    val entry = remember { viewModel.lastPlacedOrder.value }
    val currentBarista = remember { viewModel.currentBarista.value }
    val dateFormat = remember { SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receipt", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeBrown)
            )
        },
        containerColor = WarmCream
    ) { innerPadding ->
        if (entry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No receipt available", color = LightText)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    //Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "BrewMate",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CoffeeBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Order Receipt",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            dateFormat.format(Date(entry.timestamp)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                        Text(
                            "Order #${entry.orderId.take(8).uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LightText
                        )
                        currentBarista?.let {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Served by ${it.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LightText
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Divider(color = LatteAccent)
                    Spacer(Modifier.height(16.dp))

                    // Itemized list
                    entry.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkText
                                )
                                Text(
                                    "${item.quantity} × $${String.format("%.2f", item.unitPrice)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LightText
                                )
                            }
                            Text(
                                "$${String.format("%.2f", item.unitPrice * item.quantity)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Divider(color = LatteAccent)
                    Spacer(Modifier.height(12.dp))

                    SummaryRow(label = "Sub Total", value = "$${String.format("%.2f", entry.subtotal)}")
                    Spacer(Modifier.height(6.dp))
                    SummaryRow(label = "Tax", value = "$${String.format("%.2f", entry.tax)}")

                    Spacer(Modifier.height(12.dp))
                    Divider(color = LatteAccent)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL", style = MaterialTheme.typography.titleLarge, color = DarkText, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", entry.total)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeBrown
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Thank you for your order!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            PrintReceiptButton(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown)
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}