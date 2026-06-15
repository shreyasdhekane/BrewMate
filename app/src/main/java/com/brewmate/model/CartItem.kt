package com.brewmate.model

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val coffee: Coffee,
    val menuItemId: String,
    val milkType: MilkType,
    val addOns: Set<AddOnType>,
    val quantity: Int
) {
    val description: String
        get() = coffee.getDescription()

    val unitPrice: Double
        get() = coffee.getCost()

    val totalPrice: Double
        get() = unitPrice * quantity
}