package com.brewmate.model

import java.util.UUID

data class OrderHistoryItem(
    val menuItemId: String,
    val description: String,
    val unitPrice: Double,
    val quantity: Int
)

data class OrderHistoryEntry(
    val orderId: String = UUID.randomUUID().toString(),
    val timestamp: Long,
    val items: List<OrderHistoryItem>,
    val subtotal: Double,
    val tax: Double,
    val total: Double
)